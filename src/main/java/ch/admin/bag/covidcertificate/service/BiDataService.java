package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.BiDataException;
import ch.admin.bag.covidcertificate.api.response.BiDataDto;
import ch.admin.bag.covidcertificate.api.response.BiDataResponseDto;
import ch.admin.bag.covidcertificate.domain.BiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ch.admin.bag.covidcertificate.api.Constants.DATES_NOT_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.WRITING_CSV_RESULT_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiDataService {

    private final KpiDataRepository kpiDataRepository;

    public BiDataResponseDto loadBiData(LocalDate fromDate, LocalDate toDate) throws BiDataException {

        validateDateRange(fromDate, toDate);

        LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
        List<BiData> biDataList = this.kpiDataRepository.findAllByDateRange(fromDateTime, toDateTime);
        List<BiDataDto> biDataDtoList = biDataList.stream().map(this::convert).collect(Collectors.toList());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            byte[] csv = createCsvResponse(biDataDtoList);
            var entry = new ZipEntry("kpi_prod_export_" +
                    fromDateTime.format(DateTimeFormatter.ISO_DATE) +
                    "-" +
                    toDateTime.format(DateTimeFormatter.ISO_DATE) +
                    ".csv");
            entry.setSize(csv.length);
            zos.putNextEntry(entry);
            zos.write(csv);
            zos.closeEntry();
            zos.close();
            return new BiDataResponseDto(baos.toByteArray());
        } catch (IOException ex) {
            log.error("IOException creating CSV response", ex);
            throw new BiDataException(WRITING_CSV_RESULT_FAILED);
        }
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new BiDataException(DATES_NOT_VALID);
        }
        if (!fromDate.isBefore(toDate)) {
            throw new BiDataException(DATES_NOT_VALID);
        }
        if (!(fromDate.plusMonths(1l).minusDays(1).isEqual(toDate) || fromDate.plusDays(6l).isEqual(toDate))) {
            // time range is not one month or not one week
            throw new BiDataException(DATES_NOT_VALID);
        }
    }

    private byte[] createCsvResponse(List<BiDataDto> biDataDtos) throws IOException {
        var returnFile = writeCsv(biDataDtos, Charset.defaultCharset());
        return Files.readAllBytes(returnFile.toPath());
    }

    private File writeCsv(List<BiDataDto> biDataDtos, Charset charset) throws IOException {
        var randomUUID = UUID.randomUUID();
        var file = File.createTempFile("bi_data_" + randomUUID, ".csv");
        try (var csvWriter = new CSVWriter(new FileWriter(file, charset))) {
            StatefulBeanToCsv<BiDataDto> beanToCsv = new StatefulBeanToCsvBuilder<BiDataDto>(csvWriter)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withApplyQuotesToAll(true)
                    .build();
            beanToCsv.write(biDataDtos);
            return file;
        } catch (Exception e) {
            Files.delete(file.toPath());
            throw new BiDataException(WRITING_CSV_RESULT_FAILED);
        }
    }

    private BiDataDto convert(BiData biData) {
        return new BiDataDto(biData.getId(),
                biData.getTimestamp(),
                biData.getType(),
                biData.getValue(),
                biData.getDetails(),
                biData.getCountry(),
                biData.getSystemSource(),
                biData.getApiGatewayId(),
                biData.getInAppDeliveryCode(),
                biData.getFraud(),
                biData.getKeyIdentifier());
    }
}
