package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.AMBIGUOUS_SIGNING_CERTIFICATE;
import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.SIGNING_CERTIFICATE_MISSING;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigningInformationService {
    private final SigningInformationCacheService signingInformationCacheService;

    public SigningInformation getVaccinationSigningInformation(VaccinationCertificateCreateDto createDto) {
        return getVaccinationSigningInformation(createDto, LocalDate.now());
    }

    public SigningInformation getVaccinationSigningInformation(VaccinationCertificateCreateDto createDto, LocalDate validAt) {
        var medicinalProductCode = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();
        var signingInformation = signingInformationCacheService.findSigningInformation(SigningCertificateCategory.VACCINATION.value, medicinalProductCode, validAt);

        if (signingInformation == null) {
            log.error("No signing certificate was found to sign the certificate for the {} vaccine.", medicinalProductCode);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        }
        return signingInformation;
    }

    public SigningInformation getVaccinationTouristSigningInformation() {
        return getVaccinationTouristSigningInformation(LocalDate.now());
    }

    public SigningInformation getVaccinationTouristSigningInformation(LocalDate validAt) {
        var signingInformationList = signingInformationCacheService.findSigningInformation(SigningCertificateCategory.VACCINATION_TOURIST_CH.value, validAt);

        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the vaccination tourist certificate.");
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error("Ambiguous signing certificate. Multiple signing certificates were found to sign the vaccination tourist certificate.");
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }

    public SigningInformation getTestSigningInformation() {
        return getTestSigningInformation(LocalDate.now());
    }

    public SigningInformation getTestSigningInformation(LocalDate validAt) {
        var signingInformationList = signingInformationCacheService.findSigningInformation(SigningCertificateCategory.TEST.value, validAt);
        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the test certificate.");
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error("Ambiguous signing certificate. Multiple signing certificates were found to sign the test certificate");
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }

    public SigningInformation getRecoverySigningInformation(RecoveryCertificateCreateDto createDto) {
        return getRecoverySigningInformation(createDto, LocalDate.now());
    }

    public SigningInformation getRecoverySigningInformation(RecoveryCertificateCreateDto createDto, LocalDate validAt) {
        var countryOfTest = createDto.getRecoveryInfo().get(0).getCountryOfTest();
        var signingCertificateCategory = SigningCertificateCategory.RECOVERY_NON_CH.value;
        if (Objects.equals(countryOfTest, Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND)) {
            signingCertificateCategory = SigningCertificateCategory.RECOVERY_CH.value;
        }
        var signingInformationList = signingInformationCacheService.findSigningInformation(signingCertificateCategory, validAt);

        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the recovery certificate for positive test in {}.", countryOfTest);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error("Ambiguous signing certificate. Multiple signing certificates were found for positive test in {}.", countryOfTest);
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }

    public SigningInformation getAntibodySigningInformation(AntibodyCertificateCreateDto createDto) {
        return getAntibodySigningInformation(createDto, LocalDate.now());
    }

    public SigningInformation getAntibodySigningInformation(AntibodyCertificateCreateDto createDto, LocalDate validAt) {
        var signingCertificateCategory = SigningCertificateCategory.ANTIBODY_CH.value;
        var signingInformationList = signingInformationCacheService.findSigningInformation(signingCertificateCategory, validAt);

        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the antibody certificate in {}.", ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error("Ambiguous signing certificate. Multiple signing certificates were found to sign the antibody certificate in {}.", ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }

    public SigningInformation getExceptionalSigningInformation(ExceptionalCertificateCreateDto createDto) {
        return getExceptionalSigningInformation(createDto, LocalDate.now());
    }

    public SigningInformation getExceptionalSigningInformation(ExceptionalCertificateCreateDto createDto, LocalDate validAt) {
        var signingCertificateCategory = SigningCertificateCategory.EXCEPTIONAL_CH.value;
        var signingInformationList = signingInformationCacheService.findSigningInformation(signingCertificateCategory, validAt);

        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the antibody certificate in {}.", ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error("Ambiguous signing certificate. Multiple signing certificates were found to sign the exceptional certificate in {}.", ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }
}
