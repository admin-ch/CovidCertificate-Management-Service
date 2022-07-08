package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateGeneratePdfFromExistingService {

    private final CovidCertificatePdfGenerateRequestDtoMapperService pdfDtoMapperService;
    private final PdfCertificateGenerationService pdfCertificateGenerationService;

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toVaccinationCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getVaccinationInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            VaccinationTouristCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService
                .toVaccinationTouristCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getVaccinationTouristInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toTestCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getTestInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toRecoveryCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getRecoveryInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            RecoveryRatCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toRecoveryRatCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getTestInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            AntibodyCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toAntibodyCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getAntibodyInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    public CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            ExceptionalCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {

        var pdfData = pdfDtoMapperService.toExceptionalCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                                                    pdfGenerateRequestDto.getHcert(),
                                                    pdfGenerateRequestDto.getIssuedAt(),
                                                    pdfGenerateRequestDto.getDecodedCert()
                                                                         .getExceptionalInfo()
                                                                         .get(0)
                                                                         .getIdentifier());
    }

    private CovidCertificateResponseEnvelope generateFromExistingCovidCertificate(
            AbstractCertificatePdf pdfData,
            String hcert,
            long issuedAtMillis,
            String uvci) {

        try {
            var issuedAt = getLocalDateTimeFromEpochMillis(issuedAtMillis);
            var barcode = new DefaultBarcodeCreator().create(hcert, StandardCharsets.US_ASCII);
            var pdf = pdfCertificateGenerationService.generateCovidCertificate(pdfData, hcert, issuedAt);
            var responseDto = new CovidCertificateCreateResponseDto(pdf, barcode.getImage(), uvci);
            return new CovidCertificateResponseEnvelope(responseDto, null);
        } catch (BarcodeException e) {
            throw new CreateCertificateException(CREATE_BARCODE_FAILED);
        }
    }

    private LocalDateTime getLocalDateTimeFromEpochMillis(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).toLocalDateTime();
    }
}
