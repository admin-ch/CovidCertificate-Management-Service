package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.digg.dgc.encoding.BarcodeException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateGenerationController {

    private static final String CREATE_LOG = "Certificate created with: {}";

    private final SecurityHelper securityHelper;
    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final ServletJeapAuthorization jeapAuthorization;
    private final KpiDataService kpiLogService;

    @PostMapping("/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(@Valid @RequestBody VaccinationCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for vaccination certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        logKpi(KPI_TYPE_VACCINATION, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/test")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createTestCertificate(@Valid @RequestBody TestCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for test certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        logKpi(KPI_TYPE_TEST, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/recovery")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createRecoveryCertificate(@Valid @RequestBody RecoveryCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for recovery certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        logKpi(KPI_TYPE_RECOVERY, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/fromexisting/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateVaccinationPdfFromExistingCertificate(@Valid @RequestBody VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) throws BarcodeException {
        log.info("Call of Create for vaccination certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/test")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateTestPdfFromExistingCertificate(@Valid @RequestBody TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) throws BarcodeException {
        log.info("Call of Create for test certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/recovery")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateRecoveryPdfFromExistingCertificate(@Valid @RequestBody RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) throws BarcodeException {
        log.info("Call of Create for recovery certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }
    private void logKpi(String type, String uvci) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null && token.getClaimAsString(USER_EXT_ID_CLAIM_KEY) != null) {
            LocalDateTime kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {} {}", kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)), kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_TYPE_KEY, type), kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
            kpiLogService.log(new KpiData(kpiTimestamp, type, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY), uvci));
        }
    }
}
