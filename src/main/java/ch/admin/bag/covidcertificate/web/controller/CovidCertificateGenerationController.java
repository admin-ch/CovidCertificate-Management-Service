package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateGenerationController {

    private static final String CREATE_LOG = "Certificate created with: {}";

    private final CovidCertificateGenerationService covidCertificateGenerationService;
    private final CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;
    private final KpiDataService kpiLogService;

    @PostMapping("/vaccination")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(
            @Valid @RequestBody VaccinationCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for vaccination certificate");

        covidCertificateVaccinationValidationService.validateProductAndCountry(createDto);
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logVaccinationCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/vaccination-tourist")
    public CovidCertificateCreateResponseDto createVaccinationTouristCertificate(
            @Valid @RequestBody VaccinationTouristCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for vaccination tourist certificate");

        covidCertificateVaccinationValidationService.validateProductAndCountryForVaccinationTourist(createDto);
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logVaccinationTouristCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/test")
    public CovidCertificateCreateResponseDto createTestCertificate(
            @Valid @RequestBody TestCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for test certificate");

        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logTestCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/recovery")
    public CovidCertificateCreateResponseDto createRecoveryCertificate(
            @Valid @RequestBody RecoveryCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for recovery certificate");

        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logRecoveryCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/recovery-rat")
    public CovidCertificateCreateResponseDto createRecoveryRatCertificate(
            @Valid @RequestBody RecoveryRatCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for recovery-rat certificate");

        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logRecoveryRatCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/antibody")
    public CovidCertificateCreateResponseDto createAntibodyCertificate(
            @Valid @RequestBody AntibodyCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for antibody certificate");

        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logAntibodyCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }

    @PostMapping("/exceptional")
    public CovidCertificateCreateResponseDto createMedicalExemptionCertificate(
            @Valid @RequestBody ExceptionalCertificateCreateDto createDto)
            throws IOException {

        log.info("Call of create for exceptional certificate");

        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateCovidCertificate(createDto);
        CovidCertificateCreateResponseDto responseDto = responseEnvelope.getResponseDto();
        log.debug(CREATE_LOG, responseDto.getUvci());
        kpiLogService.logExceptionalCertificateGenerationKpi(
                createDto,
                responseDto.getUvci(),
                responseEnvelope.getUsedKeyIdentifier());
        return responseDto;
    }
}
