package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CertificateType;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCsvBean;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCsvBean;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCsvBean;
import ch.admin.bag.covidcertificate.api.response.CsvResponseDto;
import ch.admin.bag.covidcertificate.service.CsvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_CERTIFICATE_TYPE;
import static ch.admin.bag.covidcertificate.api.Constants.NOT_A_CSV;

@RestController
@RequestMapping("/api/v1/covidcertificate")
@RequiredArgsConstructor
@Slf4j
public class CsvController {

    private static final String CSV_CONTENT_TYPE = "text/csv";

    private final SecurityHelper securityHelper;
    private final CsvService csvService;

    @PostMapping("/csv")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CsvResponseDto createWithCsv(@RequestParam("file") MultipartFile file, @RequestParam("certificateType") CertificateType certificateType, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        if (!CSV_CONTENT_TYPE.equals(file.getContentType())) {
            throw new CreateCertificateException(NOT_A_CSV);
        }

        switch (certificateType) {
            case recovery:
                return new CsvResponseDto(csvService.handleCsvRequest(file, RecoveryCertificateCsvBean.class));
            case test:
                return new CsvResponseDto(csvService.handleCsvRequest(file, TestCertificateCsvBean.class));
            case vaccination:
                return new CsvResponseDto(csvService.handleCsvRequest(file, VaccinationCertificateCsvBean.class));
            default:
                throw new CreateCertificateException(INVALID_CERTIFICATE_TYPE);
        }
    }
}
