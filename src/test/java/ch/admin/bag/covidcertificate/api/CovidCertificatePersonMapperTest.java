package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CovidCertificatePersonMapper;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CovidCertificatePersonMapperTest {

    private final JFixture jFixture = new JFixture();

    private final String givenName = "GivenName";
    private final String familyName = "FamilyName";
    private final String dateOfBirth = jFixture.create(LocalDate.class).format(LOCAL_DATE_FORMAT);

    @Test
    public void mapsFamilyName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getFamilyName(), actual.getName().getFamilyName());
    }


    @Test
    public void mapsGivenName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getGivenName(), actual.getName().getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    @Disabled("This test checks that an exception is thrown if the standardised name exceeds the maximum number of characters. " +
            "The new version of the library we use truncates the name and thus the exception is never thrown, " +
            "since the standardised name never exceeds the maximum.")
    public void testInvalidStandardisedGivenName() {
        final var personDto = new CovidCertificatePersonDto(
                new CovidCertificatePersonNameDto(
                        familyName,
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜabcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUV"
                ),
                dateOfBirth
        );
        var exception = assertThrows(CreateCertificateException.class,
                () -> CovidCertificatePersonMapper.toCovidCertificatePerson(personDto));
        assertEquals(INVALID_STANDARDISED_GIVEN_NAME, exception.getError());
    }

    @Test
    @Disabled("This test checks that an exception is thrown if the standardised name exceeds the maximum number of characters. " +
            "The new version of the library we use truncates the name and thus the exception is never thrown, " +
            "since the standardised name never exceeds the maximum.")
    public void testInvalidStandardisedFamilyName() {
        final var personDto = new CovidCertificatePersonDto(
                new CovidCertificatePersonNameDto(
                        "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜabcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUV",
                        givenName
                ),
                dateOfBirth
        );
        var exception = assertThrows(CreateCertificateException.class,
                () -> CovidCertificatePersonMapper.toCovidCertificatePerson(personDto));
        assertEquals(INVALID_STANDARDISED_FAMILY_NAME, exception.getError());
    }
}
