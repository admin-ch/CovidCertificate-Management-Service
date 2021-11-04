package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
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
    private final CovidCertificateDtoMapperService covidCertificateDtoMapperService;
    private final CovidCertificatePdfGenerateRequestDtoMapperService covidCertificatePdfGenerateRequestDtoMapperService;
    private final CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper;
    private final SigningInformationService signingInformationService;

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toVaccinationCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                pdfGenerateRequestDto.getHcert(),
                pdfGenerateRequestDto.getIssuedAt(),
                pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toTestCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                pdfGenerateRequestDto.getHcert(),
                pdfGenerateRequestDto.getIssuedAt(),
                pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toRecoveryCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                pdfGenerateRequestDto.getHcert(),
                pdfGenerateRequestDto.getIssuedAt(),
                pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0).getIdentifier());
    }

    public CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(AntibodyCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var pdfData = covidCertificatePdfGenerateRequestDtoMapperService.toAntibodyCertificatePdf(pdfGenerateRequestDto);
        return generateFromExistingCovidCertificate(pdfData,
                pdfGenerateRequestDto.getHcert(),
                pdfGenerateRequestDto.getIssuedAt(),
                pdfGenerateRequestDto.getDecodedCert().getAntibodyInfo().get(0).getIdentifier());
    }

    private CovidCertificateCreateResponseDto generateFromExistingCovidCertificate(AbstractCertificatePdf pdfData, String hcert, long issuedAtMillis, String uvci) {
        try {
            var issuedAt = getLocalDateTimeFromEpochMillis(issuedAtMillis);
            var barcode = new DefaultBarcodeCreator().create(hcert, StandardCharsets.US_ASCII);
            var pdf = pdfCertificateGenerationService.generateCovidCertificate(pdfData, hcert, issuedAt);
            return new CovidCertificateCreateResponseDto(pdf, barcode.getImage(), uvci);
        } catch (BarcodeException e) {
            throw new CreateCertificateException(CREATE_BARCODE_FAILED);
        }
    }

    private LocalDateTime getLocalDateTimeFromEpochMillis(long millis) {
        var instant = Instant.ofEpochMilli(millis);
        return ZonedDateTime.from(instant.atZone(ZoneId.systemDefault())).toLocalDateTime();
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(VaccinationCertificateCreateDto createDto) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toVaccinationCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toVaccinationCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getVaccinationSigningInformation(createDto);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getVaccinationInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(TestCertificateCreateDto createDto) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toTestCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toTestCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getTestSigningInformation();
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getTestInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(RecoveryCertificateCreateDto createDto) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toRecoveryCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toRecoveryCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getRecoverySigningInformation(createDto);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getRecoveryInfo().get(0).getIdentifier(), createDto, signingInformation);
    }

    public CovidCertificateCreateResponseDto generateCovidCertificate(AntibodyCertificateCreateDto createDto) throws JsonProcessingException {
        var qrCodeData = covidCertificateDtoMapperService.toAntibodyCertificateQrCode(createDto);
        var pdfData = covidCertificateDtoMapperService.toAntibodyCertificatePdf(createDto, qrCodeData);
        var signingInformation = signingInformationService.getAntibodySigningInformation(createDto);
        return generateCovidCertificate(qrCodeData, pdfData, qrCodeData.getAntibodyInfo().get(0).getIdentifier(), createDto, signingInformation);
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
        var pdf = pdfCertificateGenerationService.generateCovidCertificate(pdfData, code.getPayload(), LocalDateTime.now());

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
