package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.AMBIGUOUS_SIGNING_CERTIFICATE;
import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.SIGNING_CERTIFICATE_MISSING;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigningInformationService {
    private final SigningInformationCacheService signingInformationCacheService;

    public SigningInformationDto getVaccinationSigningInformation(VaccinationCertificateCreateDto createDto) {
        return getVaccinationSigningInformation(createDto, LocalDate.now());
    }

    public SigningInformationDto getVaccinationSigningInformation(
            VaccinationCertificateCreateDto createDto, LocalDate validAt) {
        var medicinalProductCode = createDto.getCertificateData().get(0).getMedicinalProductCode();
        var signingInformation = signingInformationCacheService.findSigningInformation(
                SigningCertificateCategory.VACCINATION.value, medicinalProductCode, validAt);

        if (signingInformation == null) {
            log.error("No signing certificate was found to sign the certificate for the {} vaccine.",
                      medicinalProductCode);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        }
        return signingInformation;
    }

    public SigningInformationDto getVaccinationTouristSigningInformation() {
        return getVaccinationTouristSigningInformation(LocalDate.now());
    }

    public SigningInformationDto getVaccinationTouristSigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.VACCINATION_TOURIST_CH, validAt,
                                                       ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    public SigningInformationDto getTestSigningInformation() {
        return getTestSigningInformation(LocalDate.now());
    }

    public SigningInformationDto getTestSigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.TEST, validAt, "");
    }

    public SigningInformationDto getRecoverySigningInformation(RecoveryCertificateCreateDto createDto) {
        return getRecoverySigningInformation(createDto, LocalDate.now());
    }

    public SigningInformationDto getRecoverySigningInformation(
            RecoveryCertificateCreateDto createDto, LocalDate validAt) {
        var countryOfTest = createDto.getRecoveryInfo().get(0).getCountryOfTest();
        if (Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equals(countryOfTest)) {
            return findAndValidateSingleSigningInformation(SigningCertificateCategory.RECOVERY_CH, validAt,
                                                           ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        }
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.RECOVERY_NON_CH, validAt,
                                                       countryOfTest);
    }

    public SigningInformationDto getRecoveryRatSigningInformation() {
        return getRecoveryRatSigningInformation(LocalDate.now());
    }

    public SigningInformationDto getRecoveryRatSigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.RECOVERY_RAT_CH, validAt,
                                                       ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    public SigningInformationDto getAntibodySigningInformation() {
        return getAntibodySigningInformation(LocalDate.now());
    }

    public SigningInformationDto getAntibodySigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.ANTIBODY_CH, validAt,
                                                       ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    public SigningInformationDto getExceptionalSigningInformation() {
        return getExceptionalSigningInformation(LocalDate.now());
    }

    public SigningInformationDto getExceptionalSigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.EXCEPTIONAL_CH, validAt,
                                                       ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    public SigningInformationDto getVaccinationConversionSigningInformation() {
        return getVaccinationConversionSigningInformation(LocalDate.now());
    }

    public SigningInformationDto getVaccinationConversionSigningInformation(LocalDate validAt) {
        return findAndValidateSingleSigningInformation(SigningCertificateCategory.VACCINATION_CONVERTED, validAt,
                                                       ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    private SigningInformationDto findAndValidateSingleSigningInformation(
            SigningCertificateCategory signingCertificateCategory, LocalDate validAt, String country) {

        var signingInformationList = signingInformationCacheService.findSigningInformation(
                signingCertificateCategory.value, validAt);
        if (signingInformationList == null || signingInformationList.isEmpty()) {
            log.error("No signing certificate was found to sign the {} certificate in {}.",
                      signingCertificateCategory.value, country);
            throw new CreateCertificateException(SIGNING_CERTIFICATE_MISSING);
        } else if (signingInformationList.size() > 1) {
            log.error(
                    "Ambiguous signing certificate. Multiple signing certificates were found to sign the {} certificate in {}.",
                    signingCertificateCategory.value, country);
            throw new CreateCertificateException(AMBIGUOUS_SIGNING_CERTIFICATE);
        }
        return signingInformationList.get(0);
    }
}