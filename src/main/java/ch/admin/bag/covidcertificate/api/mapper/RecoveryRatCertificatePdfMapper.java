package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryRatCertificateCreateDto recoveryRatCertificateCreateDto,
            RecoveryCertificateQrCode qrCodeData,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        LocalDate sampleDate = recoveryRatCertificateCreateDto.getTestInfo().get(0).getSampleDateTime().withZoneSameInstant(SWISS_TIMEZONE).toLocalDate();
        return new RecoveryCertificatePdf(
                recoveryRatCertificateCreateDto.getPersonData().getName().getFamilyName(),
                recoveryRatCertificateCreateDto.getPersonData().getName().getGivenName(),
                recoveryRatCertificateCreateDto.getPersonData().getDateOfBirth(),
                recoveryRatCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                sampleDate,
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(sampleDate),
                DateHelper.calculateValidUntilForRecoveryCertificate(sampleDate),
                qrCodeData.getRecoveryInfo().get(0).getIdentifier());
    }
}
