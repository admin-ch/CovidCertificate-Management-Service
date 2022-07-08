package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.service.CovidCertificateGeneratePdfFromExistingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/covidcertificate/fromexisting")
@RequiredArgsConstructor
@Slf4j
public class CovidCertificateGeneratePdfFromExistingController {

    private final CovidCertificateGeneratePdfFromExistingService covidCertificateGenerationService;

    @PostMapping("/vaccination")
    public CovidCertificateCreateResponseDto generateVaccinationPdfFromExistingCertificate(
            @Valid @RequestBody VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for vaccination certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/vaccination-tourist")
    public CovidCertificateCreateResponseDto generateVaccinationTouristPdfFromExistingCertificate(
            @Valid @RequestBody VaccinationTouristCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for vaccination-tourist certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/test")
    public CovidCertificateCreateResponseDto generateTestPdfFromExistingCertificate(
            @Valid @RequestBody TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for test certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/recovery")
    public CovidCertificateCreateResponseDto generateRecoveryPdfFromExistingCertificate(
            @Valid @RequestBody RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for recovery certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/recovery-rat")
    public CovidCertificateCreateResponseDto generateRecoveryRatPdfFromExistingCertificate(
            @Valid @RequestBody RecoveryRatCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for recovery-rat certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/antibody")
    public CovidCertificateCreateResponseDto generateAntibodyPdfFromExistingCertificate(
            @Valid @RequestBody AntibodyCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for antibody certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }

    @PostMapping("/exceptional")
    public CovidCertificateCreateResponseDto generateExceptionalPdfFromExistingCertificate(
            @Valid @RequestBody ExceptionalCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        log.info("Call of create PDF for exceptional certificate");
        CovidCertificateResponseEnvelope responseEnvelope = covidCertificateGenerationService
                .generateFromExistingCovidCertificate(pdfGenerateRequestDto);
        return responseEnvelope.getResponseDto();
    }
}
