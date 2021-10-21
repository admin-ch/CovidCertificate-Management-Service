package ch.admin.bag.covidcertificate.web.controller.test;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import ch.admin.bag.covidcertificate.service.test.TestCovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.web.controller.SecurityHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final SecurityHelper securityHelper;
    private final SigningClient signingClient;
    private final SigningInformationRepository signingInformationRepository;
    private final TestCovidCertificateGenerationService testCovidCertificateGenerationService;

    @GetMapping("/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<SigningInformation> testSigningInformationConfiguration(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            HttpServletRequest request) {
        securityHelper.authorizeUser(request);

        List<SigningInformation> errors = new ArrayList<>();
        for(SigningCertificateCategory signingCertificateCategory: SigningCertificateCategory.values()) {
            var signingInformationList = signingInformationRepository.findSigningInformation(signingCertificateCategory.value, validAt);

            for (SigningInformation signingInformation : signingInformationList) {
                try {
                    signingClient.createSignature(UUID.randomUUID().toString().getBytes(), signingInformation);
                } catch (Exception e) {
                    errors.add(signingInformation);
                }
            }
        }
        return errors;
    }

    @PostMapping("/vaccination/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createVaccinationCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody VaccinationCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/test/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createTestCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody TestCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }

    @PostMapping("/recovery/{validAt}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CovidCertificateCreateResponseDto createRecoveryCertificate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validAt,
            @Valid @RequestBody RecoveryCertificateCreateDto createDto, HttpServletRequest request) throws IOException {
        securityHelper.authorizeUser(request);
        createDto.validate();
        return testCovidCertificateGenerationService.generateCovidCertificate(createDto, validAt);
    }
}
