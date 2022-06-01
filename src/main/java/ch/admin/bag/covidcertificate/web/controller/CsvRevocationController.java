package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.response.CsvRevocationResponseDto;
import ch.admin.bag.covidcertificate.service.CsvRevocationService;
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
@RequestMapping("/api/v1/revocation")
@RequiredArgsConstructor
@Slf4j
public class CsvRevocationController {

    private final CsvRevocationService csvRevocationService;

    @PostMapping("/csv")
    public CsvRevocationResponseDto revokeWithCsv(@RequestParam("file") MultipartFile file) throws IOException {
        log.info(file.getContentType());
        if (!CSV_CONTENT_TYPE.equals(file.getContentType()) && !EXCEL_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CreateCertificateException(NOT_A_CSV);
        }
        return csvRevocationService.handleCsvRequest(file);
    }
}
