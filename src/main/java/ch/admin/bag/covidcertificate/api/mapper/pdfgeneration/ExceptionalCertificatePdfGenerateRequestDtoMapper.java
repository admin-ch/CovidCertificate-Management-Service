package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.pdf.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionalCertificatePdfGenerateRequestDtoMapper {

    public static ExceptionalCertificatePdf toExceptionalCertificatePdf(
            ExceptionalCertificatePdfGenerateRequestDto exceptionalCertificateCreateDto,
            String countryOfTest,
            String countryOfTestEn
    ) {
        ZonedDateTime utcZoned = exceptionalCertificateCreateDto.getDecodedCert().getExceptionalInfo().get(0).getValidFrom();
        LocalDate swissLocalDate = utcZoned.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate();

        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new ExceptionalCertificatePdf(
                exceptionalCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                exceptionalCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                exceptionalCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                exceptionalCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                swissLocalDate,
                exceptionalCertificateCreateDto.getDecodedCert().getExceptionalInfo().get(0).getAttestationIssuer(),
                countryOfTest,
                countryOfTestEn,
                ISSUER,
                exceptionalCertificateCreateDto.getDecodedCert().getExceptionalInfo().get(0).getIdentifier());
    }
}
