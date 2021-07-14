package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.service.document.CovidPdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateGenerationService {
    private final BarcodeService barcodeService;
    private final PrintQueueClient printQueueClient;
    private final InAppDeliveryClient inAppDeliveryClient;
    private final ObjectMapper objectMapper;
    private final CovidPdfCertificateGenerationService covidPdfCertificateGenerationService;
    private final CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    private final CovidCertificatePdfGenerateRequestDtoMapperService covidCertificatePdfGenerateRequestDtoMapperService;

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto) throws BarcodeException {
        VaccinationCertificatePdf pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(pdfGenerateRequestDto);
        var issuedAtInstant = Instant.ofEpochMilli(pdfGenerateRequestDto.getIssuedAt());
        var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, pdfGenerateRequestDto.getHcert(), ZonedDateTime.from(issuedAtInstant.atZone(ZoneOffset.systemDefault())).toLocalDateTime());
        return new CovidCertificateCreateResponseDto(pdf, barcode.getImage(), pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) throws BarcodeException {
        TestCertificatePdf pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toTestCertificatePdf(pdfGenerateRequestDto);
        var issuedAtInstant = Instant.ofEpochMilli(pdfGenerateRequestDto.getIssuedAt());
        var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, pdfGenerateRequestDto.getHcert(), ZonedDateTime.from(issuedAtInstant.atZone(ZoneOffset.systemDefault())).toLocalDateTime());
        return new CovidCertificateCreateResponseDto(pdf, barcode.getImage(), pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) throws BarcodeException {
        RecoveryCertificatePdf pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(pdfGenerateRequestDto);
        var issuedAtInstant = Instant.ofEpochMilli(pdfGenerateRequestDto.getIssuedAt());
        var barcode = new DefaultBarcodeCreator().create(pdfGenerateRequestDto.getHcert(), StandardCharsets.US_ASCII);
        var pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, pdfGenerateRequestDto.getHcert(), ZonedDateTime.from(issuedAtInstant.atZone(ZoneOffset.systemDefault())).toLocalDateTime());
        return new CovidCertificateCreateResponseDto(pdf, barcode.getImage(), pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(VaccinationCertificateCreateDto createDto) throws JsonProcessingException {
        VaccinationCertificateQrCode qrCodeData = covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto);
        VaccinationCertificatePdf pdfData = covidCertificateDtoMapperService.toVaccinationCertificatePdf(createDto, qrCodeData);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getVaccinationInfo().get(0).getIdentifier(), createDto);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(TestCertificateCreateDto createDto) throws JsonProcessingException {
        TestCertificateQrCode qrCodeData = covidCertificateDtoMapperService.toTestCertificateQrCode(createDto);
        TestCertificatePdf pdfData = covidCertificateDtoMapperService.toTestCertificatePdf(createDto, qrCodeData);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getTestInfo().get(0).getIdentifier(), createDto);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(RecoveryCertificateCreateDto createDto) throws JsonProcessingException {
        RecoveryCertificateQrCode qrCodeData = covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto);
        RecoveryCertificatePdf pdfData = covidCertificateDtoMapperService.toRecoveryCertificatePdf(createDto, qrCodeData);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto);
    }

    private CovidCertificateCreateResponseDto generateCovidCertificate(AbstractCertificateQrCode qrCodeData, AbstractCertificatePdf pdfData, String uvci, CertificateCreateDto createDto) throws JsonProcessingException {
        String contents = objectMapper.writer().writeValueAsString(qrCodeData);
        log.info("Create barcode");
        Barcode code = barcodeService.createBarcode(contents);
        log.info("Create certificate pdf");
        byte[] pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, code.getPayload(), LocalDateTime.now());

        CovidCertificateCreateResponseDto responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
        if (createDto.sendToPrint()) {
            printQueueClient.sendPrintJob(CertificatePrintRequestDtoMapper.toCertificatePrintRequestDto(pdf, uvci, createDto));
        } else if (createDto.sendToApp()) {
            var inAppDeliveryDto = new InAppDeliveryRequestDto(createDto.getAppCode(), code.getPayload(), Base64.getEncoder().encodeToString(pdf));
            var createError = this.inAppDeliveryClient.deliverToApp(inAppDeliveryDto); // null if no error
            responseDto.setAppDeliveryError(createError);
        }
        return responseDto;
    }
}
