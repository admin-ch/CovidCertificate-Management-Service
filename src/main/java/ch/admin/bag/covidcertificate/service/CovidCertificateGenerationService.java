package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AbstractCertificateQrCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;

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
    private final CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    private final SigningInformationService signingInformationService;
    private final COSETime coseTime;

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
            var createError = this.inAppDeliveryClient.deliverToApp(
                    uvci, createDto.getSystemSource(), createDto.getUserExtId(), inAppDeliveryDto); // null if no error
            responseDto.setAppDeliveryError(createError);
        }
        return new CovidCertificateResponseEnvelope(responseDto,
                                                    signingInformation.getCalculatedKeyIdentifier());
    }
}
