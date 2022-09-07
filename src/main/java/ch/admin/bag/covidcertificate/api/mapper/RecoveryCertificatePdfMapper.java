package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryCertificatePdfMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryCertificateCreateDto recoveryCertificateCreateDto,
            RecoveryCertificateQrCode qrCodeData,
            String countryOfTest,
            String countryOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryCertificatePdf(
                recoveryCertificateCreateDto.getPersonData().getName().getFamilyName(),
                recoveryCertificateCreateDto.getPersonData().getName().getGivenName(),
                recoveryCertificateCreateDto.getPersonData().getDateOfBirth(),
                recoveryCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                recoveryCertificateCreateDto.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(recoveryCertificateCreateDto.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryCertificateCreateDto.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult()),
                qrCodeData.getRecoveryInfo().get(0).getIdentifier());
    }
}
