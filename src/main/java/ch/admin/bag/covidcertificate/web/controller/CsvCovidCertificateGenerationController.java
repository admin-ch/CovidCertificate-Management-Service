package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.response.CsvCertificateGenerationResponseDto;
import ch.admin.bag.covidcertificate.service.CsvCovidCertificateGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.NOT_A_CSV;
import static ch.admin.bag.covidcertificate.service.FileService.CSV_CONTENT_TYPE;
import static ch.admin.bag.covidcertificate.service.FileService.EXCEL_CONTENT_TYPE;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CsvCovidCertificateGenerationController {

    private final CsvCovidCertificateGenerationService csvCovidCertificateGenerationService;

    @PostMapping("/csv")
    public CsvCertificateGenerationResponseDto createWithCsv(@RequestParam("file") MultipartFile file, @RequestParam("certificateType") String certificateType) throws IOException {
        log.info(file.getContentType());
        if (!CSV_CONTENT_TYPE.equals(file.getContentType()) && !EXCEL_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CreateCertificateException(NOT_A_CSV);
        }
        return csvCovidCertificateGenerationService.handleCsvRequest(file, certificateType);
    }
}
