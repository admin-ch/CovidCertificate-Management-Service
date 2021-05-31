package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePersonName;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import se.digg.dgc.transliteration.MrzEncoder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CovidCertificatePersonMapper {

    public static CovidCertificatePerson toCovidCertificatePerson(CovidCertificatePersonDto personDto) {
        return new CovidCertificatePerson(
                toCovidCertificatePersonName(personDto.getName()),
                personDto.getDateOfBirth()
        );
    }

    private static CovidCertificatePersonName toCovidCertificatePersonName(CovidCertificatePersonNameDto name) {
        return new CovidCertificatePersonName(name.getFamilyName(), standardiseName(name.getFamilyName()), name.getGivenName(), standardiseName(name.getGivenName()));
    }

    private static String standardiseName(String name) {
        return MrzEncoder.encode(name);
    }
}
