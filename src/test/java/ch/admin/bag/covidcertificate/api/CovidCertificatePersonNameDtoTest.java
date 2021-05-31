package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class CovidCertificatePersonNameDtoTest {

    private final String givenName = "GivenName";
    private final String familyName = "FamilyName";

    @Test
    public void testInvalidGivenName() {
        CovidCertificatePersonNameDto testee = new CovidCertificatePersonNameDto(
                familyName,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_GIVEN_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                familyName,
                ""
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_GIVEN_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                familyName,
                "leisiidkfkdjaösikijeifhdiaösiefidoöasidjfdkslaösdijfieaoöosihdafdlskdjfdkslasifjdoaifdisoahfdisoahfdisoaisdhfdiosahsdifhdsao"
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_GIVEN_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                familyName,
                givenName
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void slsie() {
        String lbub = ZonedDateTime.now(SWISS_TIMEZONE).format(LOG_FORMAT);
        assertNotNull(lbub);
    }

    @Test
    public void testInvalidFamilyName() {
        CovidCertificatePersonNameDto testee = new CovidCertificatePersonNameDto(
                null,
                givenName
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_FAMILY_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                "",
                givenName
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_FAMILY_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                "leisiidkfkdjaösikijeifhdiaösiefidoöasidjfdkslaösdijfieaoöosihdafdlskdjfdkslasifjdoaifdisoahfdisoahfdisoaisdhfdiosahsdifhdsao",
                givenName
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_FAMILY_NAME, exception.getError());

        testee = new CovidCertificatePersonNameDto(
                familyName,
                givenName
        );
        assertDoesNotThrow(testee::validate);
    }
}
