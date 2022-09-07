package ch.admin.bag.covidcertificate.service.test;

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
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.BarcodeService;
import ch.admin.bag.covidcertificate.service.COSETime;
import ch.admin.bag.covidcertificate.service.CovidCertificateDtoMapperService;
import ch.admin.bag.covidcertificate.service.SigningInformationService;
import ch.admin.bag.covidcertificate.service.document.PdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificateQrCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class TestCovidCertificateGenerationService {
    private final BarcodeService barcodeService;
    private final PrintQueueClient printQueueClient;
    private final InAppDeliveryClient inAppDeliveryClient;
    private final ObjectMapper objectMapper;
    private final PdfCertificateGenerationService covidPdfCertificateGenerationService;
    private final CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    private final CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    private final SigningInformationService signingInformationService;
    private final COSETime coseTime;

    public CovidCertificateCreateResponseDto generateCovidCertificate(VaccinationCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toVaccinationCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getVaccinationSigningInformation(createDto, validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getVaccinationInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(VaccinationTouristCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toVaccinationTouristCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toVaccinationTouristCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getVaccinationTouristSigningInformation(validAt);
        var expiration30Days = coseTime.calculateExpirationInstantPlusDays(Constants.EXPIRATION_PERIOD_30_DAYS);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getVaccinationTouristInfo().get(0).getIdentifier(), createDto, signingInformation, expiration30Days);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(TestCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toTestCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toTestCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getTestSigningInformation(validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getTestInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(RecoveryCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toRecoveryCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getRecoverySigningInformation(createDto, validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(RecoveryRatCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toRecoveryRatCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toRecoveryRatCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getRecoveryRatSigningInformation(validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(AntibodyCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toAntibodyCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toAntibodyCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getAntibodySigningInformation(validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getAntibodyInfo().get(0).getIdentifier(),
                                        createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(
            ExceptionalCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toExceptionalCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toExceptionalCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getExceptionalSigningInformation(validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getExceptionalInfo().get(0).getIdentifier(),
                                        createDto, signingInformation);
    }

    private CovidCertificateCreateResponseDto generateCovidCertificate(
            AbstractCertificateQrCode qrCodeData,
            AbstractCertificatePdf pdfData,
            String uvci,
            CertificateCreateDto createDto,
            SigningInformationDto signingInformation) throws JsonProcessingException {
        var expiration24Months = coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS);
        return this.generateCovidCertificate(qrCodeData, pdfData, uvci, createDto, signingInformation,
                                             expiration24Months);
    }

    public ConvertedCertificateResponseEnvelope convertFromExistingCovidCertificate(
            VaccinationCertificateConversionRequestDto conversionDto, LocalDate validAt)
            throws JsonProcessingException {

        // map certificate data
        var qrCodeData = covidCertificateDtoMapperService
                .toVaccinationCertificateQrCodeForConversion(conversionDto);
        // take right signing information
        var signingInformation = signingInformationService
                .getVaccinationConversionSigningInformation(validAt);
        // define expiration of converted certificate
        var expiration24Months = coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS);
        // get the mapped UVCI, see toVaccinationCertificateQrCode
        var uvci = qrCodeData.getVaccinationInfo().get(0).getIdentifier();

        return generateCovidCertificate(qrCodeData, uvci, signingInformation, expiration24Months);
    }

    private ConvertedCertificateResponseEnvelope generateCovidCertificate(
            AbstractCertificateQrCode qrCodeData,
            String uvci,
            SigningInformationDto signingInformation,
            Instant expiration) throws JsonProcessingException {

        var contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.trace("Create barcode for conversion");
        var code = barcodeService.createBarcode(contents, signingInformation, expiration);
        var responseDto = new ConvertedCertificateResponseDto(code.getPayload(), uvci);
        responseDto.validate();
        return new ConvertedCertificateResponseEnvelope(
                responseDto,
                signingInformation.getCalculatedKeyIdentifier());
    }

    private CovidCertificateCreateResponseDto generateCovidCertificate(
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
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, code.getPayload(),
                                                                                LocalDateTime.now());

        var responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
        responseDto.validate();
        if (createDto.sendToPrint()) {
            printQueueClient.sendPrintJob(
                    certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(pdf, uvci, createDto));
        } else if (createDto.sendToApp()) {
            var inAppDeliveryDto = new InAppDeliveryRequestDto(createDto.getAppCode(), code.getPayload(),
                                                               Base64.getEncoder().encodeToString(pdf));
            var createError = this.inAppDeliveryClient.deliverToApp(
                    uvci, createDto.getSystemSource(), createDto.getUserExtId(), inAppDeliveryDto); // null if no error
            responseDto.setAppDeliveryError(createError);
        }
        return responseDto;
    }
}
