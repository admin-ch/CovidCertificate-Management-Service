package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CovidCertificatePersonDtoTest {

    private final CovidCertificatePersonNameDto personNameDto = mock(CovidCertificatePersonNameDto.class);
    private final LocalDate dateOfBirth = LocalDate.of(1989, Month.JANUARY, 17);

    @Test
    public void testNoPersonData() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                null,
                dateOfBirth
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidDateOfBirth() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                MIN_DATE_OF_BIRTH.minusDays(1)
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                MAX_DATE_OF_BIRTH.plusDays(1)
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth
        );
        assertDoesNotThrow(testee::validate);
    }
}
