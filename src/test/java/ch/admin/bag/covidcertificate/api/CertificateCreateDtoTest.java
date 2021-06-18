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
    private final CovidCertificateAddressDto addressDto = new CovidCertificateAddressDto("street", 1000, "city", "BE");

    private static class CertificateCreateDtoIml extends CertificateCreateDto {

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address
        ) {
            super(personData, language, address);
        }
    }

    @Test
    public void throwsCertificateCreateException__onNoPersonData() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                null,
                language,
                addressDto
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void throwsCertificateCreateException__onInvalidLanguage() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                null,
                addressDto
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "en",
                addressDto
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void doesValidateSuccessfully() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                new CovidCertificateAddressDto("street", 1000, "city", "BE")
        );
        assertDoesNotThrow(testee::validate);
    }
}
