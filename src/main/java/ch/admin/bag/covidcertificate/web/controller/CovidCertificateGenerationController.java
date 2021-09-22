package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.digg.dgc.encoding.BarcodeException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateGenerationController {

    private static final String CREATE_LOG = "Certificate created with: {}";

    private final SecurityHelper securityHelper;
    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final KpiDataService kpiLogService;

    @PostMapping("/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(@Valid @RequestBody VaccinationCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for vaccination certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logCertificateGenerationKpi(KPI_TYPE_VACCINATION, responseDto.getUvci());
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
        kpiLogService.logTestCertificateGenerationKpi(createDto, responseDto.getUvci());
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
        kpiLogService.logCertificateGenerationKpi(KPI_TYPE_RECOVERY, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/fromexisting/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateVaccinationPdfFromExistingCertificate(@Valid @RequestBody VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for vaccination certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/test")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateTestPdfFromExistingCertificate(@Valid @RequestBody TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for test certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/recovery")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateRecoveryPdfFromExistingCertificate(@Valid @RequestBody RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for recovery certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }
}
