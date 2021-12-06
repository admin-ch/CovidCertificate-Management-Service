package ch.admin.bag.covidcertificate.service.test;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
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

    public CovidCertificateCreateResponseDto generateCovidCertificate(AntibodyCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toAntibodyCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toAntibodyCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getAntibodySigningInformation(createDto, validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getAntibodyInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    private CovidCertificateCreateResponseDto generateCovidCertificate(AbstractCertificateQrCode qrCodeData,
                                                                       AbstractCertificatePdf pdfData,
                                                                       String uvci,
                                                                       CertificateCreateDto createDto,
                                                                       SigningInformation signingInformation) throws JsonProcessingException {
        var expiration24Months = coseTime.calculateExpirationInstantPlusMonths(Constants.EXPIRATION_PERIOD_24_MONTHS);
        return this.generateCovidCertificate(qrCodeData, pdfData, uvci, createDto, signingInformation, expiration24Months);
    }

    private CovidCertificateCreateResponseDto generateCovidCertificate(AbstractCertificateQrCode qrCodeData,
                                                                       AbstractCertificatePdf pdfData,
                                                                       String uvci,
                                                                       CertificateCreateDto createDto,
                                                                       SigningInformation signingInformation,
                                                                       Instant expiration) throws JsonProcessingException {
        var contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.info("Create barcode");
        var code = barcodeService.createBarcode(contents, signingInformation, expiration);
        log.info("Create certificate pdf");
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, code.getPayload(), LocalDateTime.now());

        var responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
        responseDto.validate();
        if (createDto.sendToPrint()) {
            printQueueClient.sendPrintJob(certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(pdf, uvci, createDto));
        } else if (createDto.sendToApp()) {
            var inAppDeliveryDto = new InAppDeliveryRequestDto(createDto.getAppCode(), code.getPayload(), Base64.getEncoder().encodeToString(pdf));
            var createError = this.inAppDeliveryClient.deliverToApp(inAppDeliveryDto); // null if no error
            responseDto.setAppDeliveryError(createError);
        }
        return responseDto;
    }
}
