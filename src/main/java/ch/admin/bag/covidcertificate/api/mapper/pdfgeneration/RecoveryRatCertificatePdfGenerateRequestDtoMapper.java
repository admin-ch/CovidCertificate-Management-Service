package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecoveryRatCertificatePdfGenerateRequestDtoMapper {

    public static RecoveryCertificatePdf toRecoveryCertificatePdf(
            RecoveryRatCertificatePdfGenerateRequestDto recoveryRatCertificatePdfGenerateRequestDto,
            String memberStateOfTest,
            String memberStateOfTestEn
    ) {
        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        LocalDate sampleDate = recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getSampleDateTime().withZoneSameInstant(SWISS_TIMEZONE).toLocalDate();
        return new RecoveryCertificatePdf(
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getName().getGivenName(),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getPersonData().getDateOfBirth(),
                recoveryRatCertificatePdfGenerateRequestDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                sampleDate,
                memberStateOfTest,
                memberStateOfTestEn,
                ISSUER,
                DateHelper.calculateValidFrom(sampleDate),
                DateHelper.calculateValidUntilForRecoveryCertificate(sampleDate),
                recoveryRatCertificatePdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getIdentifier());
    }
}
