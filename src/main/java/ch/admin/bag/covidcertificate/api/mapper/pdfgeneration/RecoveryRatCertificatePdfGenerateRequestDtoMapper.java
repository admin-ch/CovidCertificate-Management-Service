package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfGenerateRequestDtoMapper {

    public static RecoveryRatCertificatePdf toRecoveryRatCertificatePdf(
            RecoveryRatCertificatePdfGenerateRequestDto recoveryRatCertificatePdfGenerateRequestDto,
            IssuableTestDto rapidTestDto,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryRatCertificatePdf(
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getGivenName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getDateOfBirth(),
                recoveryRatCertificatePdfGenerateRequestDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                rapidTestDto.getTestType().typeDisplay,
                rapidTestDto.getDisplay(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTestingCentreOrFacility(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }
}
