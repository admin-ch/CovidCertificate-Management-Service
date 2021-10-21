package ch.admin.bag.covidcertificate.service.test;

import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.service.BarcodeService;
import ch.admin.bag.covidcertificate.service.CovidCertificateDtoMapperService;
import ch.admin.bag.covidcertificate.service.CovidCertificatePdfGenerateRequestDtoMapperService;
import ch.admin.bag.covidcertificate.service.SigningInformationService;
import ch.admin.bag.covidcertificate.service.document.CovidPdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificateQrCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final CovidPdfCertificateGenerationService covidPdfCertificateGenerationService;
    private final CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    private final CovidCertificatePdfGenerateRequestDtoMapperService covidCertificatePdfGenerateRequestDtoMapperService;
    private final CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    private final SigningInformationService signingInformationService;

    public CovidCertificateCreateResponseDto generateCovidCertificate(VaccinationCertificateCreateDto createDto, LocalDate validAt) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toVaccinationCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getVaccinationSigningInformation(createDto, validAt);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getVaccinationInfo().get(0).getIdentifier(), createDto, signingInformation);
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

    private CovidCertificateCreateResponseDto generateCovidCertificate(AbstractCertificateQrCode qrCodeData,
                                                                       AbstractCertificatePdf pdfData,
                                                                       String uvci,
                                                                       CertificateCreateDto createDto,
                                                                       SigningInformation signingInformation) throws JsonProcessingException {
        var contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.info("Create barcode");
        var code = barcodeService.createBarcode(contents, signingInformation);
        log.info("Create certificate pdf");
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, code.getPayload(), LocalDateTime.now());

        var responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
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
