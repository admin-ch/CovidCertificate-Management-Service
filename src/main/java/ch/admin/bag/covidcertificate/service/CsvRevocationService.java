package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationCsvBean;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import ch.admin.bag.covidcertificate.api.response.CsvRevocationResponseDto;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UnicodeBOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI_IN_REQUEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV_SIZE;
import static ch.admin.bag.covidcertificate.api.Constants.WRITING_RETURN_CSV_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvRevocationService {
    private final FileService fileService;
    private final RevocationService revocationService;

    public CsvRevocationResponseDto handleCsvRequest(MultipartFile file) throws IOException {
        final var charset = Charset.forName(UniversalDetector.detectCharset(file.getInputStream()));
        log.debug("Found charset {} for file", charset);

        List<RevocationCsvBean> csvBeans = mapFileToBean(file, charset);
        checkSize(csvBeans);
        List<UvciForRevocationDto> dtos = mapBeansToDtos(csvBeans);
        checkDuplicates(dtos);

        RevocationListResponseDto responseDto = revocationService.performMassRevocation(
                new RevocationListDto(dtos, SystemSource.CsvUpload)
        );

        // set error as status or default to status OK
        for (RevocationCsvBean bean : csvBeans) {
            bean.setStatus(
                    responseDto.getUvcisToErrorMessage().getOrDefault(bean.getUvci(), RevocationCsvBean.STATUS_OK)
            );
        }

        byte[] csv = createCsvResponse(csvBeans, charset);
        return new CsvRevocationResponseDto(
                (int) csvBeans.stream().filter(c -> !Objects.equals(c.getStatus(), RevocationCsvBean.STATUS_OK)).count(),
                (int) csvBeans.stream().filter(c -> Objects.equals(c.getStatus(), RevocationCsvBean.STATUS_OK)).count(),
                csv
        );
    }

    private byte[] createCsvResponse(List<RevocationCsvBean> csvBeans, Charset charset) throws IOException {
        var returnFile = writeCsv(csvBeans, charset);
        return Files.readAllBytes(returnFile.toPath());
    }

    private List<RevocationCsvBean> mapFileToBean(MultipartFile file, Charset charset) throws IOException {
        var separator = fileService.getSeparator(file);
        try (Reader reader = new BufferedReader(new InputStreamReader(
                new UnicodeBOMInputStream(file.getInputStream()), charset))) {

            CsvToBean<RevocationCsvBean> csvToBean = new CsvToBeanBuilder<RevocationCsvBean>(reader)
                    .withSeparator(separator)
                    .withType(RevocationCsvBean.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();

        } catch (Exception ex) {
            throw new RevocationException(INVALID_CSV);
        }
    }

    private List<UvciForRevocationDto> mapBeansToDtos(List<RevocationCsvBean> csvBeans) {
        return csvBeans
                .stream()
                .map(csvBean -> {
                    try {
                        return csvBean.mapToDto();
                    } catch (RevocationException e) {
                        csvBean.setStatus(e.getError().toString());
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private File writeCsv(List<RevocationCsvBean> certificateCsvBeans, Charset charset) throws IOException {
        var tempId = UUID.randomUUID();
        var file = File.createTempFile("temp"+tempId, ".csv");
        try (var csvWriter = new CSVWriter(new FileWriter(file, charset))) {
            StatefulBeanToCsv<RevocationCsvBean> beanToCsv = new StatefulBeanToCsvBuilder<RevocationCsvBean>(csvWriter)
                    .withSeparator(';')
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (Exception e) {
            Files.delete(file.toPath());
            throw new RevocationException(WRITING_RETURN_CSV_FAILED);
        }
    }

    private void checkSize(List<RevocationCsvBean> csvBeans) {
        if (csvBeans.size() < RevocationListDto.MIN_SIZE_LIST || csvBeans.size() > RevocationListDto.MAX_SIZE_LIST) {
            throw new RevocationException(INVALID_CSV_SIZE);
        }
    }

    private void checkDuplicates(List<UvciForRevocationDto> dtos) {
        if (dtos.stream().map(UvciForRevocationDto::getUvci).collect(Collectors.toSet()).size() < dtos.size()) {
            throw new RevocationException(DUPLICATE_UVCI_IN_REQUEST);
        }
    }
}
