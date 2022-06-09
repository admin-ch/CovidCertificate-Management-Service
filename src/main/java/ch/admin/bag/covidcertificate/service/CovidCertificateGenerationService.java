package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
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
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificateQrCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Base64;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateGenerationService {
    private final BarcodeService barcodeService;
    private final PrintQueueClient printQueueClient;
    private final InAppDeliveryClient inAppDeliveryClient;
    private final ObjectMapper objectMapper;
    private final PdfCertificateGenerationService pdfCertificateGenerationService;
    private final CovidCertificateDtoMapperService ccDtoMapperService;
    private final CovidCertificatePdfGenerateRequestDtoMapperService pdfDtoMapperService;
    private final CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    private final SigningInformationService signingInformationService;
    private final COSETime coseTime;

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

    public CovidCertificateResponseEnvelope generateCovidCertificate(VaccinationCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toVaccinationCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toVaccinationCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService
                .getVaccinationSigningInformation(createDto);
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getVaccinationInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(VaccinationTouristCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toVaccinationTouristCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService
                .toVaccinationTouristCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getVaccinationTouristSigningInformation();
        var expiration30Days = coseTime.calculateExpirationInstantPlusDays(Constants.EXPIRATION_PERIOD_30_DAYS);
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getVaccinationTouristInfo().get(0).getIdentifier(),
                createDto,
                signingInformation,
                expiration30Days);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(TestCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toTestCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toTestCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getTestSigningInformation();
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getTestInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(RecoveryCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toRecoveryCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toRecoveryCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getRecoverySigningInformation(createDto);
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getRecoveryInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(RecoveryRatCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toRecoveryRatCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toRecoveryRatCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getRecoveryRatSigningInformation();
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getRecoveryInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(AntibodyCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toAntibodyCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toAntibodyCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getAntibodySigningInformation();
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getAntibodyInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    public CovidCertificateResponseEnvelope generateCovidCertificate(ExceptionalCertificateCreateDto createDto)
            throws JsonProcessingException {

        var qrCodeData = ccDtoMapperService.toExceptionalCertificateQrCode(createDto);
        var pdfData = ccDtoMapperService.toExceptionalCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getExceptionalSigningInformation();
        return generateCovidCertificate(
                qrCodeData,
                pdfData,
                qrCodeData.getExceptionalInfo().get(0).getIdentifier(),
                createDto,
                signingInformation);
    }

    private CovidCertificateResponseEnvelope generateCovidCertificate(
            AbstractCertificateQrCode qrCodeData,
            AbstractCertificatePdf pdfData,
            String uvci,
            CertificateCreateDto createDto,
            SigningInformationDto signingInformation)
            throws JsonProcessingException {

        var expiration24Months = coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS);
        return this.generateCovidCertificate(
                qrCodeData,
                pdfData,
                uvci,
                createDto,
                signingInformation,
                expiration24Months);
    }

    private CovidCertificateResponseEnvelope generateCovidCertificate(
            AbstractCertificateQrCode qrCodeData,
            AbstractCertificatePdf pdfData,
            String uvci,
            CertificateCreateDto createDto,
            SigningInformationDto signingInformation,
            Instant expiration) throws JsonProcessingException {

        var contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.trace("Create barcode");
        var code = barcodeService.createBarcode(contents, signingInformation, expiration);
        log.trace("Create certificate pdf");
        var pdf = pdfCertificateGenerationService.generateCovidCertificate(
                pdfData,
                code.getPayload(),
                LocalDateTime.now());

        var responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
        responseDto.validate();
        if (createDto.sendToPrint()) {
            printQueueClient.sendPrintJob(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(
                    pdf,
                    uvci,
                    createDto));
        } else if (createDto.sendToApp()) {
            var inAppDeliveryDto = new InAppDeliveryRequestDto(createDto.getAppCode(), code.getPayload(),
                                                               Base64.getEncoder().encodeToString(pdf));
            var createError = this.inAppDeliveryClient.deliverToApp(uvci, inAppDeliveryDto); // null if no error
            responseDto.setAppDeliveryError(createError);
        }
        var envelope = new CovidCertificateResponseEnvelope(responseDto,
                                                            signingInformation.getCalculatedKeyIdentifier());
        return envelope;
    }
}
