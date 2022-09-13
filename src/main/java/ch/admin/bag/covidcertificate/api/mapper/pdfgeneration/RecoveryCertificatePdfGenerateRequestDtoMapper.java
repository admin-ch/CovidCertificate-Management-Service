package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryCertificatePdfGenerateRequestDtoMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryCertificatePdfGenerateRequestDto recoveryCertificateCreateDto,
            String countryOfTest,
            String countryOfTestEn
    ) {
        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new RecoveryCertificatePdf(
                recoveryCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                recoveryCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                recoveryCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                recoveryCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                recoveryCertificateCreateDto.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(recoveryCertificateCreateDto.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult()),
                DateHelper.calculateValidUntilForRecoveryCertificate(recoveryCertificateCreateDto.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult()),
                recoveryCertificateCreateDto.getDecodedCert().getRecoveryInfo().get(0).getIdentifier());
    }
}
