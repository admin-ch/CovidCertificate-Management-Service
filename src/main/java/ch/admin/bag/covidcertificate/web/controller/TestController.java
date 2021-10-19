package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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
}
