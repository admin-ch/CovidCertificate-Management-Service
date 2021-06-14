package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private final SecurityHelper securityHelper;
    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final ServletJeapAuthorization jeapAuthorization;
    private final KpiDataService kpiLogService;

    @PostMapping("/csv")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public @ResponseBody
    byte[] createWithCsv(@RequestParam("file") MultipartFile file, @RequestParam("certificateType") CertificateType certificateType, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        if (CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CreateCertificateException(NOT_A_CSV);
        }
        switch (certificateType) {
            case recovery:
                List<CertificateCreateDto> recoveryCreateDtos = mapToCreateDtos(file, RecoveryCertificateCsvBean.class);
                createRecoveryCertificates(recoveryCreateDtos.stream().map(createDto -> (RecoveryCertificateCreateDto) createDto).collect(Collectors.toList()));
                break;
            case test:
                List<CertificateCreateDto> testCreateDtos = mapToCreateDtos(file, TestCertificateCsvBean.class);
                createTestCertificates(testCreateDtos.stream().map(createDto -> (TestCertificateCreateDto) createDto).collect(Collectors.toList()));
                break;
            case vaccination:
                List<CertificateCreateDto> vaccinationCreateDtos = mapToCreateDtos(file, VaccinationCertificateCsvBean.class);
                createVaccinationCertificates(vaccinationCreateDtos.stream().map(createDto -> (VaccinationCertificateCreateDto) createDto).collect(Collectors.toList()));
                break;
        }
        log.debug("Parsing, validation and creation successful");

        return file.getBytes();
    }

    private List<CertificateCreateDto> mapToCreateDtos(MultipartFile file, Class<?> csvBeanClass) {
        List<CertificateCsvBean> certificateCsvBeans;
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            CsvToBean<CertificateCsvBean> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(csvBeanClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            certificateCsvBeans = csvToBean.parse();

        } catch (Exception ex) {
            log.error("CSV parsing was not successful", ex);
            throw new CreateCertificateException(INVALID_CSV);
        }
        if (certificateCsvBeans.size() < MIN_CSV_ROWS || certificateCsvBeans.size() > MAX_CSV_ROWS) {
            throw new CreateCertificateException(INVALID_CSV_SIZE);
        }
        List<CertificateCreateDto> createDtos = certificateCsvBeans
                .stream()
                .map(CertificateCsvBean::mapToCreateDto)
                .collect(Collectors.toList());
        createDtos.forEach(CertificateCreateDto::validate);
        return createDtos;
    }

    private void createRecoveryCertificates(List<RecoveryCertificateCreateDto> createDtos) {
        log.info("Call of Create for recovery certificate");
        createDtos.forEach(createDto -> {
            try {
                CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
                log.debug("Certificate created with: {}", responseDto.getUvci());
                logKpi(KPI_TYPE_RECOVERY);
            } catch (JsonProcessingException e) {
                // ToDo throw right exception
            }
        });

    }

    private void createTestCertificates(List<TestCertificateCreateDto> createDtos) {
        log.info("Call of Create for test certificate");
        createDtos.forEach(createDto -> {
            try {
                CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
                log.debug("Certificate created with: {}", responseDto.getUvci());
                logKpi(KPI_TYPE_TEST);
            } catch (JsonProcessingException e) {
                // ToDo throw right exception
            }
        });

    }

    private void createVaccinationCertificates(List<VaccinationCertificateCreateDto> createDtos) {
        createDtos.forEach(createDto -> {
            log.info("Call of Create for vaccination certificate");
            try {
                CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
                log.debug("Certificate created with: {}", responseDto.getUvci());
                logKpi(KPI_TYPE_VACCINATION);
            } catch (JsonProcessingException e) {
                // ToDo throw right exception
            }
        });

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
