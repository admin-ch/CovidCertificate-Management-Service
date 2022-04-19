package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfGenerateRequestDtoMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryRatCertificatePdfGenerateRequestDto recoveryRatCertificatePdfGenerateRequestDto,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryCertificatePdf(
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getGivenName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getDateOfBirth(),
                recoveryRatCertificatePdfGenerateRequestDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime().toLocalDate(),
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime().toLocalDate()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime().toLocalDate()),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }
}
