package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.CsvError;
import ch.admin.bag.covidcertificate.api.exception.CsvException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CsvResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvService {

    private static final int MIN_CSV_ROWS = 1;
    private static final int MAX_CSV_ROWS = 100;
    private static final String PDF_FILE_NAME_PREFIX = "covid-certificate-";

    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final ServletJeapAuthorization jeapAuthorization;
    private final KpiDataService kpiLogService;
    private final ValueSetsService valueSetsService;

    public CsvResponseDto handleCsvRequest(MultipartFile file, String certificateType) throws IOException {
        CertificateType validCertificateType;
        try {
            validCertificateType = CertificateType.valueOf(certificateType);
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
            default:
                throw new CreateCertificateException(INVALID_CERTIFICATE_TYPE);
        }
    }

    private byte[] handleCsvRequest(MultipartFile file, Class<? extends CertificateCsvBean> csvBeanClass) throws IOException {
        List<CertificateCsvBean> csvBeans = mapToBean(file, csvBeanClass);
        checkSize(csvBeans);
        List<CertificateCreateDto> createDtos = mapToCreateDtos(csvBeans);
        if (areCreateCertificateRequestsValid(createDtos, csvBeans)) {
            List<CovidCertificateCreateResponseDto> responseDtos = createCertificates(createDtos, csvBeanClass);
            return zipGeneratedCertificates(getPdfMap(responseDtos));
        } else {
            return createCsvException(csvBeans);
        }
    }

    private List<CovidCertificateCreateResponseDto> createCertificates(List<CertificateCreateDto> createDtos, Class<?> csvBeanClass) throws JsonProcessingException {
        if (csvBeanClass == RecoveryCertificateCsvBean.class) {
            return createRecoveryCertificates(createDtos.stream().map(createDto -> (RecoveryCertificateCreateDto) createDto).collect(Collectors.toList()));
        } else if (csvBeanClass == TestCertificateCsvBean.class) {
            return createTestCertificates(createDtos.stream().map(createDto -> (TestCertificateCreateDto) createDto).collect(Collectors.toList()));
        } else if (csvBeanClass == VaccinationCertificateCsvBean.class) {
            return createVaccinationCertificates(createDtos.stream().map(createDto -> (VaccinationCertificateCreateDto) createDto).collect(Collectors.toList()));
        } else {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private byte[] createCsvException(List<CertificateCsvBean> csvBeans) throws IOException {
        File returnFile = writeCsv(csvBeans);
        byte[] errorCsv = Files.readAllBytes(returnFile.toPath());
        returnFile.delete();
        throw new CsvException(new CsvError(INVALID_CREATE_REQUESTS, errorCsv));
    }

    private List<CovidCertificateCreateResponseDto> createRecoveryCertificates(List<RecoveryCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        for (RecoveryCertificateCreateDto createDto : createDtos) {
            log.info("Call of Create for recovery certificate");
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            logUvci(responseDto.getUvci());
            logKpi(KPI_TYPE_RECOVERY);
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
            logKpi(KPI_TYPE_TEST);
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
            logKpi(KPI_TYPE_VACCINATION);
        }
        return responseDtos;
    }

    private List<CertificateCsvBean> mapToBean(MultipartFile file, Class<? extends CertificateCsvBean> csvBeanClass) throws IOException {
        char separator = getSeparator(file);
        String encoding = UniversalDetector.detectCharset(file.getInputStream());
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {

            CsvToBean<CertificateCsvBean> csvToBean = new CsvToBeanBuilder<CertificateCsvBean>(reader)
                    .withSeparator(separator)
                    .withType(csvBeanClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<CertificateCsvBean> beans = csvToBean.parse();
            reader.close();
            return beans;

        } catch (Exception ex) {
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private char getSeparator(MultipartFile file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line = reader.readLine();
        reader.close();
        if (line.contains(",") && !line.contains("\t") && !line.contains(";")) {
            return ',';
        } else if (line.contains("\t") && !line.contains(",") && !line.contains(";")) {
            return '\t';
        } else if (line.contains(";") && !line.contains(",") && !line.contains("\t")){
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
        boolean hasError = false;
        for (int i = 0; i < createDtos.size(); i++) {
            CertificateCreateDto createDto = createDtos.get(i);
            CertificateCsvBean csvBean = csvBeans.get(i);
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
            RecoveryCertificateDataDto dataDto = ((RecoveryCertificateCreateDto) createDto).getRecoveryInfo().get(0);
            CountryCode countryCode = valueSetsService.getCountryCode(dataDto.getCountryOfTest(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
            }
        } else if (createDto instanceof TestCertificateCreateDto) {
            TestCertificateDataDto dataDto = ((TestCertificateCreateDto) createDto).getTestInfo().get(0);
            CountryCode countryCode = valueSetsService.getCountryCode(dataDto.getMemberStateOfTest(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
            }
            valueSetsService.getTestValueSet(dataDto);
        } else if (createDto instanceof VaccinationCertificateCreateDto) {
            VaccinationCertificateDataDto dataDto = ((VaccinationCertificateCreateDto) createDto).getVaccinationInfo().get(0);
            CountryCode countryCode = valueSetsService.getCountryCode(dataDto.getCountryOfVaccination(), createDto.getLanguage());
            if (countryCode == null) {
                throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
            }
            valueSetsService.getVaccinationValueSet(dataDto.getMedicinalProductCode());
        }
    }

    private File writeCsv(List<CertificateCsvBean> certificateCsvBeans) {
        UUID tempId = UUID.randomUUID();
        File file = new File("temp" + tempId + ".csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            StatefulBeanToCsv<CertificateCsvBean> beanToCsv = new StatefulBeanToCsvBuilder<CertificateCsvBean>(csvWriter)
                    .withSeparator(';')
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (Exception e) {
            file.delete();
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
        String extension = ".pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (Map.Entry<String, byte[]> fileNameAndContentEntry : fileNameAndContentMap.entrySet()) {
            ZipEntry entry = new ZipEntry(fileNameAndContentEntry.getKey() + extension);
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

    private void logKpi(String type) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null && token.getClaimAsString(USER_EXT_ID_CLAIM_KEY) != null) {
            LocalDateTime kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {} {}", kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)), kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_TYPE_KEY, type), kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
            kpiLogService.log(new KpiData(kpiTimestamp, type, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
        }
    }
}
