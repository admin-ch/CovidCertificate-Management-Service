package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.CsvError;
import ch.admin.bag.covidcertificate.api.exception.CsvException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CsvResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CERTIFICATE_TYPE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CREATE_REQUESTS;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CSV_SIZE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.WRITING_RETURN_CSV_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvService {

    private static final int MIN_CSV_ROWS = 1;
    private static final int MAX_CSV_ROWS = 100;
    private static final String PDF_FILE_NAME_PREFIX = "covid-certificate-";

    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final KpiDataService kpiLogService;
    private final ValueSetsService valueSetsService;
    private final CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    public CsvResponseDto handleCsvRequest(MultipartFile file, String certificateType) throws IOException {
        CertificateType validCertificateType;
        try {
            validCertificateType = CertificateType.fromString(certificateType);
        } catch (IllegalArgumentException e) {
            throw new CreateCertificateException(INVALID_CERTIFICATE_TYPE);
        }
        switch (validCertificateType) {
            case recovery:
                return new CsvResponseDto(handleCsvRequest(file, RecoveryCertificateCsvBean.class));
            case test:
                return new CsvResponseDto(handleCsvRequest(file, TestCertificateCsvBean.class));
            case vaccination:
                return new CsvResponseDto(handleCsvRequest(file, VaccinationCertificateCsvBean.class));
            case vaccination_tourist:
                return new CsvResponseDto(handleCsvRequest(file, VaccinationTouristCertificateCsvBean.class));
            case antibody:
                return new CsvResponseDto(handleCsvRequest(file, AntibodyCertificateCsvBean.class));
            default:
                throw new CreateCertificateException(INVALID_CERTIFICATE_TYPE);
        }
    }

    private byte[] handleCsvRequest(MultipartFile file, Class<? extends CertificateCsvBean> csvBeanClass) throws IOException {
        final var charset = Charset.forName(UniversalDetector.detectCharset(file.getInputStream()));
        log.debug("Found charset {} for file", charset);
        List<CertificateCsvBean> csvBeans = mapToBean(file, csvBeanClass, charset);
        checkSize(csvBeans);
        List<CertificateCreateDto> createDtos = mapToCreateDtos(csvBeans);
        if (areCreateCertificateRequestsValid(createDtos, csvBeans)) {
            List<CovidCertificateCreateResponseDto> responseDtos = createCertificates(createDtos, csvBeanClass);
            return zipGeneratedCertificates(getPdfMap(responseDtos));
        } else {
            return createCsvException(csvBeans, charset);
        }
    }

    private List<CovidCertificateCreateResponseDto> createCertificates(List<CertificateCreateDto> createDtos, Class<?> csvBeanClass) throws JsonProcessingException {
        if (csvBeanClass == RecoveryCertificateCsvBean.class) {
            return createRecoveryCertificates(createDtos.stream().map(RecoveryCertificateCreateDto.class::cast).collect(Collectors.toList()));
        } else if (csvBeanClass == TestCertificateCsvBean.class) {
            return createTestCertificates(createDtos.stream().map(TestCertificateCreateDto.class::cast).collect(Collectors.toList()));
        } else if (csvBeanClass == VaccinationCertificateCsvBean.class) {
            return createVaccinationCertificates(createDtos.stream().map(VaccinationCertificateCreateDto.class::cast).collect(Collectors.toList()));
        } else if (csvBeanClass == AntibodyCertificateCsvBean.class) {
            return createAntibodyCertificates(createDtos.stream().map(AntibodyCertificateCreateDto.class::cast).collect(Collectors.toList()));
        } else {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private byte[] createCsvException(List<CertificateCsvBean> csvBeans, Charset charset) throws IOException {
        var returnFile = writeCsv(csvBeans, charset);
        byte[] errorCsv = Files.readAllBytes(returnFile.toPath());
        Files.delete(returnFile.toPath());
        throw new CsvException(new CsvError(INVALID_CREATE_REQUESTS, errorCsv));
    }

    private List<CovidCertificateCreateResponseDto> createRecoveryCertificates(List<RecoveryCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        for (RecoveryCertificateCreateDto createDto : createDtos) {
            log.info("Call of Create for recovery certificate");
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            logUvci(responseDto.getUvci());
            kpiLogService.logRecoveryCertificateGenerationKpi(createDto, responseDto.getUvci());
        }
        return responseDtos;
    }

    private List<CovidCertificateCreateResponseDto> createTestCertificates(List<TestCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        for (TestCertificateCreateDto createDto : createDtos) {
            log.info("Call of Create for test certificate");
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            logUvci(responseDto.getUvci());
            kpiLogService.logTestCertificateGenerationKpi(createDto, responseDto.getUvci());
        }
        return responseDtos;
    }

    private List<CovidCertificateCreateResponseDto> createVaccinationCertificates(List<VaccinationCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        for (VaccinationCertificateCreateDto createDto : createDtos) {
            log.info("Call of Create for vaccination certificate");
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            logUvci(responseDto.getUvci());
            kpiLogService.logVaccinationCertificateGenerationKpi(createDto, responseDto.getUvci());
        }
        return responseDtos;
    }

    private List<CovidCertificateCreateResponseDto> createAntibodyCertificates(List<AntibodyCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        for (AntibodyCertificateCreateDto createDto : createDtos) {
            log.info("Call of Create for antibody certificate");
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            logUvci(responseDto.getUvci());
            kpiLogService.logAntibodyCertificateGenerationKpi(responseDto.getUvci());
        }
        return responseDtos;
    }

    private List<CertificateCsvBean> mapToBean(MultipartFile file, Class<? extends CertificateCsvBean> csvBeanClass, Charset charset) throws IOException {
        var separator = getSeparator(file);
        try (Reader reader = new BufferedReader(new InputStreamReader(new UnicodeBOMInputStream(file.getInputStream()), charset))) {

            CsvToBean<CertificateCsvBean> csvToBean = new CsvToBeanBuilder<CertificateCsvBean>(reader)
                    .withSeparator(separator)
                    .withType(csvBeanClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();

        } catch (Exception ex) {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private char getSeparator(MultipartFile file) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        var line = reader.readLine();
        reader.close();
        if (line.contains(",") && !line.contains("\t") && !line.contains(";")) {
            return ',';
        } else if (line.contains("\t") && !line.contains(",") && !line.contains(";")) {
            return '\t';
        } else if (line.contains(";") && !line.contains(",") && !line.contains("\t")) {
            return ';';
        } else {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private List<CertificateCreateDto> mapToCreateDtos(List<CertificateCsvBean> csvBeans) {
        return csvBeans
                .stream()
                .map(csvBean -> {
                    try {
                        return csvBean.mapToCreateDto();
                    } catch (CreateCertificateException e) {
                        csvBean.setError(e.getError().toString());
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    private boolean areCreateCertificateRequestsValid(List<CertificateCreateDto> createDtos, List<CertificateCsvBean> csvBeans) {
        var hasError = false;
        for (var i = 0; i < createDtos.size(); i++) {
            var createDto = createDtos.get(i);
            var csvBean = csvBeans.get(i);
            if (createDto != null && csvBean.getError() == null) {
                try {
                    validate(createDto);
                } catch (CreateCertificateException e) {
                    hasError = true;
                    csvBean.setError(e.getError().toString());
                }
            } else {
                hasError = true;
            }
        }
        return !hasError;
    }

    private void validate(CertificateCreateDto createDto) {
        createDto.validate();
        if (createDto instanceof RecoveryCertificateCreateDto) {
            var dataDto = ((RecoveryCertificateCreateDto) createDto).getRecoveryInfo().get(0);
            var countryCode = valueSetsService.getCountryCode(dataDto.getCountryOfTest(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
            }
        } else if (createDto instanceof TestCertificateCreateDto) {
            var dataDto = ((TestCertificateCreateDto) createDto).getTestInfo().get(0);
            var countryCode = valueSetsService.getCountryCode(dataDto.getMemberStateOfTest(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
            }
            valueSetsService.getIssuableTestDto(dataDto);
        } else if (createDto instanceof VaccinationCertificateCreateDto) {
            var dataDto = ((VaccinationCertificateCreateDto) createDto).getVaccinationInfo().get(0);
            var countryCode = valueSetsService.getCountryCode(dataDto.getCountryOfVaccination(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
            }
            covidCertificateVaccinationValidationService.validateProductAndCountry((VaccinationCertificateCreateDto) createDto);
        } else if (createDto instanceof AntibodyCertificateCreateDto) {
            // no validation
        }
    }

    private File writeCsv(List<CertificateCsvBean> certificateCsvBeans, Charset charset) throws IOException {
        var tempId = UUID.randomUUID();
        var file = new File("temp" + tempId + ".csv");
        try (var csvWriter = new CSVWriter(new FileWriter(file, charset))) {
            StatefulBeanToCsv<CertificateCsvBean> beanToCsv = new StatefulBeanToCsvBuilder<CertificateCsvBean>(csvWriter)
                    .withSeparator(';')
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (Exception e) {
            Files.delete(file.toPath());
            throw new CreateCertificateException(WRITING_RETURN_CSV_FAILED);
        }
    }

    private void checkSize(List<CertificateCsvBean> csvBeans) {
        if (csvBeans.size() < MIN_CSV_ROWS || csvBeans.size() > MAX_CSV_ROWS) {
            throw new CreateCertificateException(INVALID_CSV_SIZE);
        }
    }

    private String getCertificateFileName(String uvci) {
        return PDF_FILE_NAME_PREFIX + uvci.replace(":", "_");
    }

    private Map<String, byte[]> getPdfMap(List<CovidCertificateCreateResponseDto> responseDtos) {
        Map<String, byte[]> responseMap = new HashMap<>();
        for (CovidCertificateCreateResponseDto responseDto : responseDtos) {
            String certificateFileName = getCertificateFileName(responseDto.getUvci());
            responseMap.put(certificateFileName, responseDto.getPdf());
        }
        return responseMap;
    }

    private byte[] zipGeneratedCertificates(Map<String, byte[]> fileNameAndContentMap) throws IOException {
        var extension = ".pdf";
        var baos = new ByteArrayOutputStream();
        var zos = new ZipOutputStream(baos);
        for (Map.Entry<String, byte[]> fileNameAndContentEntry : fileNameAndContentMap.entrySet()) {
            var entry = new ZipEntry(fileNameAndContentEntry.getKey() + extension);
            entry.setSize(fileNameAndContentEntry.getValue().length);
            zos.putNextEntry(entry);
            zos.write(fileNameAndContentEntry.getValue());
        }
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

    private void logUvci(String uvci) {
        log.debug("Certificate created with: {}", uvci);
    }
}
