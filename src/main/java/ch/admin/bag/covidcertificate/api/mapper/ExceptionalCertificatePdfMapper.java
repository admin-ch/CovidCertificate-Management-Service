package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificatePdf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionalCertificatePdfMapper {

    public static ExceptionalCertificatePdf toExceptionalCertificatePdf(
            ExceptionalCertificateCreateDto exceptionalCertificateCreateDto,
            ExceptionalCertificateQrCode qrCodeData,
            String countryOfTest,
            String countryOfTestEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new ExceptionalCertificatePdf(
                exceptionalCertificateCreateDto.getPersonData().getName().getFamilyName(),
                exceptionalCertificateCreateDto.getPersonData().getName().getGivenName(),
                exceptionalCertificateCreateDto.getPersonData().getDateOfBirth(),
                exceptionalCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                exceptionalCertificateCreateDto.getExceptionalInfo().get(0).getValidFrom(),
                exceptionalCertificateCreateDto.getExceptionalInfo().get(0).getAttestationIssuer(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                qrCodeData.getExceptionalInfo().get(0).getIdentifier());
    }
}
