package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final String language = "de";

    private static class CertificateCreateDtoIml extends CertificateCreateDto {
        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language
        ) {
            super(personData, language);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address
        ) {
            super(personData, language, address);
        }
    }

    @Test
    public void testNoPersonData() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                null,
                language
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidLanguage() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "en"
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidAddress() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto(null, null, 1000, "test")
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 0, "test")
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void testAddressZipCode() {
        // test low
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 999, "test")
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 1000, "test")
        );
        assertDoesNotThrow(testee::validate);

        // test high
        testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 10000, "test")
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 9999, "test")
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testValidAddress() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", null, 1000, "test")
        );
        assertDoesNotThrow(testee::validate);

        testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("test", "test", 1000, "test")
        );
        assertDoesNotThrow(testee::validate);
    }
}
