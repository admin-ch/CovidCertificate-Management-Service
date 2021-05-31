package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_LANGUAGE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_PERSON_DATA;
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
}
