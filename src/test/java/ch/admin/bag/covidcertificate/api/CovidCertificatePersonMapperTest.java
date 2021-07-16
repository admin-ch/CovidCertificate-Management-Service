package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.CovidCertificatePersonMapper;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import com.flextrade.jfixture.JFixture;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_STANDARDISED_FAMILY_NAME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_STANDARDISED_GIVEN_NAME;
import static org.junit.jupiter.api.Assertions.*;

public class CovidCertificatePersonMapperTest {

    private final JFixture jFixture = new JFixture();

    private final String givenName = "GivenName";
    private final String familyName = "FamilyName";
    private final LocalDate dateOfBirth = jFixture.create(LocalDate.class);

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
    @Ignore
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
    @Ignore
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
