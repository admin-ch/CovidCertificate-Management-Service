package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CsvController {

    private static final int MIN_CSV_ROWS = 1;
    private static final int MAX_CSV_ROWS = 100;
    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String PDF_FILE_NAME_PREFIX = "covid-certificate-";

    private final SecurityHelper securityHelper;
    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final ServletJeapAuthorization jeapAuthorization;
    private final KpiDataService kpiLogService;

    @PostMapping("/csv")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public byte[] createWithCsv(@RequestParam("file") MultipartFile file, @RequestParam("certificateType") CertificateType certificateType, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CreateCertificateException(NOT_A_CSV);
        }
        switch (certificateType) {
            case recovery:
                return handleRecoveryRequest(file);
            case test:
                return handleTestRequest(file);
            case vaccination:
                return handleVaccinationRequest(file);
        }
        return file.getBytes();
    }

    private byte[] handleRecoveryRequest(MultipartFile file) throws IOException {
        List<CertificateCsvBean> csvBeans = mapToBean(file, RecoveryCertificateCsvBean.class);
        if (csvBeans.size() < MIN_CSV_ROWS || csvBeans.size() > MAX_CSV_ROWS) {
            throw new CreateCertificateException(INVALID_CSV_SIZE);
        }
        List<CertificateCreateDto> createDtos = mapToCreateDto(csvBeans);
        if (isCreateCertificateValid(createDtos, csvBeans)) {
            List<CovidCertificateCreateResponseDto> responseDtos = createRecoveryCertificates(createDtos.stream().map(createDto -> (RecoveryCertificateCreateDto) createDto).collect(Collectors.toList()));
            return zipGeneratedCertificates(getPdfMap(responseDtos, createDtos));
        } else {
            File returnFile = writeRecoveryCsv(csvBeans.stream().map(csvBean -> (RecoveryCertificateCsvBean) csvBean).collect(Collectors.toList()));
            return Files.readAllBytes(returnFile.toPath());
        }
    }

    private byte[] handleTestRequest(MultipartFile file) throws IOException {
        List<CertificateCsvBean> csvBeans = mapToBean(file, TestCertificateCsvBean.class);
        if (csvBeans.size() < MIN_CSV_ROWS || csvBeans.size() > MAX_CSV_ROWS) {
            throw new CreateCertificateException(INVALID_CSV_SIZE);
        }
        List<CertificateCreateDto> createDtos = mapToCreateDto(csvBeans);
        if (isCreateCertificateValid(createDtos, csvBeans)) {
            List<CovidCertificateCreateResponseDto> responseDtos = createTestCertificates(createDtos.stream().map(createDto -> (TestCertificateCreateDto) createDto).collect(Collectors.toList()));
            return zipGeneratedCertificates(getPdfMap(responseDtos, createDtos));
        } else {
            File returnFile = writeTestCsv(csvBeans.stream().map(csvBean -> (TestCertificateCsvBean) csvBean).collect(Collectors.toList()));
            return Files.readAllBytes(returnFile.toPath());
        }
    }

    private byte[] handleVaccinationRequest(MultipartFile file) throws IOException {
        List<CertificateCsvBean> csvBeans = mapToBean(file, VaccinationCertificateCsvBean.class);
        if (csvBeans.size() < MIN_CSV_ROWS || csvBeans.size() > MAX_CSV_ROWS) {
            throw new CreateCertificateException(INVALID_CSV_SIZE);
        }
        List<CertificateCreateDto> createDtos = mapToCreateDto(csvBeans);
        if (isCreateCertificateValid(createDtos, csvBeans)) {
            List<CovidCertificateCreateResponseDto> responseDtos = createVaccinationCertificates(createDtos.stream().map(createDto -> (VaccinationCertificateCreateDto) createDto).collect(Collectors.toList()));
            return zipGeneratedCertificates(getPdfMap(responseDtos, createDtos));
        } else {
            File returnFile = writeVaccinationCsv(csvBeans.stream().map(csvBean -> (VaccinationCertificateCsvBean) csvBean).collect(Collectors.toList()));
            return Files.readAllBytes(returnFile.toPath());
        }
    }

    private List<CertificateCsvBean> mapToBean(MultipartFile file, Class<?> csvBeanClass) {
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            CsvToBean<CertificateCsvBean> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(csvBeanClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            return csvToBean.parse();

        } catch (Exception ex) {
            log.error("CSV parsing was not successful", ex);
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private List<CertificateCreateDto> mapToCreateDto(List<CertificateCsvBean> csvBeans) {
        return csvBeans
                .stream()
                .map(CertificateCsvBean::mapToCreateDto)
                .collect(Collectors.toList());
    }

    private boolean isCreateCertificateValid(List<CertificateCreateDto> createDtos, List<CertificateCsvBean> csvBeans) {
        boolean hasError = false;
        for (int i = 0; i < createDtos.size(); i++) {
            try {
                createDtos.get(i).validate();
            } catch (CreateCertificateException e) {
                hasError = true;
                csvBeans.get(i).setError(e.getError().toString());
            }
        }
        return !hasError;
    }

    private File writeRecoveryCsv(List<RecoveryCertificateCsvBean> certificateCsvBeans) {
        File file = new File("temp.csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            StatefulBeanToCsv<RecoveryCertificateCsvBean> beanToCsv =
                    new StatefulBeanToCsvBuilder<RecoveryCertificateCsvBean>(csvWriter).build();

            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error("Write CSV failed", e);
            // ToDo throw right exception
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private File writeTestCsv(List<TestCertificateCsvBean> certificateCsvBeans) {
        File file = new File("temp.csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            StatefulBeanToCsv<TestCertificateCsvBean> beanToCsv =
                    new StatefulBeanToCsvBuilder<TestCertificateCsvBean>(csvWriter).build();

            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error("Write CSV failed", e);
            // ToDo throw right exception
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private File writeVaccinationCsv(List<VaccinationCertificateCsvBean> certificateCsvBeans) {
        File file = new File("temp.csv");
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(file))) {
            StatefulBeanToCsv<VaccinationCertificateCsvBean> beanToCsv =
                    new StatefulBeanToCsvBuilder<VaccinationCertificateCsvBean>(csvWriter).build();

            beanToCsv.write(certificateCsvBeans);
            return file;
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            log.error("Write CSV failed", e);
            // ToDo throw right exception
            throw new CreateCertificateException(INVALID_CSV);
        }
    }

    private List<CovidCertificateCreateResponseDto> createRecoveryCertificates(List<RecoveryCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        log.info("Call of Create for recovery certificate");
        for (RecoveryCertificateCreateDto createDto : createDtos) {
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            log.debug("Certificate created with: {}", responseDto.getUvci());
            logKpi(KPI_TYPE_RECOVERY);
        }
        return responseDtos;
    }

    private List<CovidCertificateCreateResponseDto> createTestCertificates(List<TestCertificateCreateDto> createDtos) throws JsonProcessingException {
        List<CovidCertificateCreateResponseDto> responseDtos = new ArrayList<>();
        log.info("Call of Create for test certificate");
        for (TestCertificateCreateDto createDto : createDtos) {
            CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
            responseDtos.add(responseDto);
            log.debug("Certificate created with: {}", responseDto.getUvci());
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
            log.debug("Certificate created with: {}", responseDto.getUvci());
            logKpi(KPI_TYPE_VACCINATION);
        }
        return responseDtos;
    }

    private String getCertificateFileName(String givenName, String familyName) {
        return PDF_FILE_NAME_PREFIX + givenName + "-" + familyName;
    }

    private Map<String, byte[]> getPdfMap(List<CovidCertificateCreateResponseDto> responseDtos, List<CertificateCreateDto> createDtos) {
        Map<String, byte[]> responseMap = new HashMap<>();
        for (int i = 0; i < responseDtos.size(); i++) {
            CovidCertificatePersonNameDto nameDto = createDtos.get(i).getPersonData().getName();
            responseMap.put(getCertificateFileName(nameDto.getGivenName(), nameDto.getFamilyName()), responseDtos.get(i).getPdf());
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

    private void logKpi(String type) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null && token.getClaimAsString(USER_EXT_ID_CLAIM_KEY) != null) {
            LocalDateTime kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {} {}", kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)), kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_TYPE_KEY, type), kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
            kpiLogService.log(new KpiData(kpiTimestamp, type, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
        }
    }
}
