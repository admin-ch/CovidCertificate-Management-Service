package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateGenerationController {

    private static final String CREATE_LOG = "Certificate created with: {}";

    private final SecurityHelper securityHelper;
    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;
    private final KpiDataService kpiLogService;

    @PostMapping("/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(@Valid @RequestBody VaccinationCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for vaccination certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        covidCertificateVaccinationValidationService.validateProductAndCountry(createDto);
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logVaccinationCertificateGenerationKpi(createDto, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/vaccination-tourist")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationTouristCertificate(@Valid @RequestBody VaccinationTouristCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for vaccination tourist certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        covidCertificateVaccinationValidationService.validateProductAndCountryForVaccinationTourist(createDto);
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logVaccinationTouristCertificateGenerationKpi(createDto, responseDto.getUvci());
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
        kpiLogService.logRecoveryCertificateGenerationKpi(createDto, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/recovery-rat")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createRecoveryRatCertificate(@Valid @RequestBody RecoveryRatCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for recovery-rat certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logRecoveryRatCertificateGenerationKpi(createDto, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/antibody")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createAntibodyCertificate(@Valid @RequestBody AntibodyCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for antibody certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logAntibodyCertificateGenerationKpi(createDto, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/exceptional")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createMedicalExemptionCertificate(@Valid @RequestBody ExceptionalCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        log.info("Call of Create for exceptional certificate");
        securityHelper.authorizeUser(request);
        createDto.validate();
        CovidCertificateCreateResponseDto responseDto = covidCertificateGenerationService.generateCovidCertificate(createDto);
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logExceptionalCertificateGenerationKpi(createDto, responseDto.getUvci());
        return responseDto;
    }

    @PostMapping("/fromexisting/vaccination")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateVaccinationPdfFromExistingCertificate(@Valid @RequestBody VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for vaccination certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/vaccination-tourist")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateVaccinationTouristPdfFromExistingCertificate(@Valid @RequestBody VaccinationTouristCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for vaccination-tourist certificate");
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

    @PostMapping("/fromexisting/recovery-rat")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateRecoveryRatPdfFromExistingCertificate(@Valid @RequestBody RecoveryRatCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for recovery-rat certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/antibody")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateAntibodyPdfFromExistingCertificate(@Valid @RequestBody AntibodyCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for antibody certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }

    @PostMapping("/fromexisting/exceptional")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto generateExceptionalPdfFromExistingCertificate(@Valid @RequestBody ExceptionalCertificatePdfGenerateRequestDto pdfGenerateRequestDto, HttpServletRequest request) {
        log.info("Call of Create for exceptional certificate");
        return covidCertificateGenerationService.generateFromExistingCovidCertificate(pdfGenerateRequestDto);
    }
}
