package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePdfGeneratePersonDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePdfGeneratePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePersonName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import se.digg.dgc.transliteration.MrzEncoder;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CovidCertificatePersonMapper {

    public static CovidCertificatePerson toCovidCertificatePerson(CertificatePdfGeneratePersonDto personDto) {
        return new CovidCertificatePerson(
                toCovidCertificatePersonName(personDto.getName()),
                personDto.getDateOfBirth()
        );
    }

    private static CovidCertificatePersonName toCovidCertificatePersonName(CertificatePdfGeneratePersonNameDto name) {
        String standardisedFamilyName = standardiseName(name.getFamilyName());
        if (standardisedFamilyName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_STANDARDISED_FAMILY_NAME);
        }
        String standardisedGivenName = standardiseName(name.getGivenName());
        if (standardisedGivenName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_STANDARDISED_GIVEN_NAME);
        }
        return new CovidCertificatePersonName(name.getFamilyName(), standardisedFamilyName, name.getGivenName(), standardisedGivenName);
    }

    private static String standardiseName(String name) {
        return MrzEncoder.encode(name);
    }
}