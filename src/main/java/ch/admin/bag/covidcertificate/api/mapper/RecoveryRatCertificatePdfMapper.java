package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryRatCertificateCreateDto recoveryRatCertificateCreateDto,
            RecoveryCertificateQrCode qrCodeData,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryCertificatePdf(
                recoveryRatCertificateCreateDto.getPersonData().getName().getFamilyName(),
                recoveryRatCertificateCreateDto.getPersonData().getName().getGivenName(),
                recoveryRatCertificateCreateDto.getPersonData().getDateOfBirth(),
                recoveryRatCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                recoveryRatCertificateCreateDto.getTestInfo().get(0).getSampleDateTime().toLocalDate(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(recoveryRatCertificateCreateDto.getTestInfo().get(0).getSampleDateTime().toLocalDate()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryRatCertificateCreateDto.getTestInfo().get(0).getSampleDateTime().toLocalDate()),
                qrCodeData.getRecoveryInfo().get(0).getIdentifier());
    }
}
