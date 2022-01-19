package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfMapper {

    public static RecoveryRatCertificatePdf toRecoveryRatCertificatePdf(
            RecoveryRatCertificateCreateDto recoveryRatCertificateCreateDto,
            IssuableTestDto issuableTestDto,
            RecoveryRatCertificateQrCode qrCodeData,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryRatCertificatePdf(
                recoveryRatCertificateCreateDto.getPersonData().getName().getFamilyName(),
                recoveryRatCertificateCreateDto.getPersonData().getName().getGivenName(),
                recoveryRatCertificateCreateDto.getPersonData().getDateOfBirth(),
                recoveryRatCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                issuableTestDto.getTestType().typeDisplay,
                issuableTestDto.getDisplay(),
                recoveryRatCertificateCreateDto.getTestInfo().get(0).getSampleDateTime(),
                recoveryRatCertificateCreateDto.getTestInfo().get(0).getTestingCentreOrFacility(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                qrCodeData.getTestInfo().get(0).getIdentifier());
    }
}
