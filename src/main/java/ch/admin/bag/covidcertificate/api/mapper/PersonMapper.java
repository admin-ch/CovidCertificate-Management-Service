package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePersonName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import se.digg.dgc.transliteration.MrzEncoder;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_STANDARDISED_FAMILY_NAME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_STANDARDISED_GIVEN_NAME;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonMapper {

    public static CovidCertificatePerson toCovidCertificatePerson(CovidCertificatePersonDto personDto) {
        return new CovidCertificatePerson(
                toCovidCertificatePersonName(personDto.getName()),
                personDto.getDateOfBirth()
        );
    }

    public static CovidCertificatePerson toCertificatePerson(CertificatePersonDto personDto) {
        return new CovidCertificatePerson(
                toCertificatePersonName(personDto.getName()),
                personDto.getDateOfBirth()
        );
    }

    private static CovidCertificatePersonName toCovidCertificatePersonName(CovidCertificatePersonNameDto nameDto) {
        return standardiseAndValidateNames(nameDto.getFamilyName(), nameDto.getGivenName());
    }

    private static CovidCertificatePersonName toCertificatePersonName(CertificatePersonNameDto nameDto) {
        return standardiseAndValidateNames(nameDto.getFamilyName(), nameDto.getGivenName());
    }

    private static CovidCertificatePersonName standardiseAndValidateNames(String familyName, String givenName) {
        String standardisedFamilyName = standardiseName(familyName);
        if (standardisedFamilyName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_STANDARDISED_FAMILY_NAME);
        }
        String standardisedGivenName = standardiseName(givenName);
        if (standardisedGivenName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_STANDARDISED_GIVEN_NAME);
        }
        return new CovidCertificatePersonName(familyName, standardisedFamilyName, givenName, standardisedGivenName);
    }

    private static String standardiseName(String name) {
        return MrzEncoder.encode(name);
    }
}
