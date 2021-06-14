package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.service.document.CovidPdfCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.digg.dgc.encoding.Barcode;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateGenerationService {
    private final BarcodeService barcodeService;
    private final PrintQueueClient printQueueClient;
    private final ObjectMapper objectMapper;
    private final CovidPdfCertificateGenerationService covidPdfCertificateGenerationService;
    private final CovidCertificateDtoMapperService covidCertificateDtoMapperService;

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
        byte[] pdf = covidPdfCertificateGenerationService.generateCovidCertificate(pdfData, code);

        CovidCertificateCreateResponseDto responseDto = new CovidCertificateCreateResponseDto(pdf, code.getImage(), uvci);
        if (createDto.getAddress() != null) {
            printQueueClient.sendPrintJob(CertificatePrintRequestDtoMapper.toCertificatePrintRequestDto(pdf, uvci, createDto));
        }
        return responseDto;
    }
}
