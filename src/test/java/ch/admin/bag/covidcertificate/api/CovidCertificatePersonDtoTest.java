package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_BIRTH_IN_FUTURE;
import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.MIN_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.NO_PERSON_DATA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class CovidCertificatePersonDtoTest {

    private final CovidCertificatePersonNameDto personNameDto = mock(CovidCertificatePersonNameDto.class);
    private final LocalDate dateOfBirth = LocalDate.of(1989, Month.JANUARY, 17);

    @Test
    public void testNoPersonData() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                null,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
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
                MIN_DATE_OF_BIRTH.minusDays(1).format(LOCAL_DATE_FORMAT)
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                MAX_DATE_OF_BIRTH.plusDays(1).format(LOCAL_DATE_FORMAT)
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidDateOfBirth_oneDayInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusDays(1).format(LOCAL_DATE_FORMAT)
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH_IN_FUTURE, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(LOCAL_DATE_FORMAT)
        );
        assertDoesNotThrow(testee::validate);

    }

    @Test
    public void testInvalidDateOfBirth_oneMonthInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH_IN_FUTURE, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidDateOfBirth_oneYearInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("yyyy"))
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_BIRTH_IN_FUTURE, exception.getError());

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
        );
        assertDoesNotThrow(testee::validate);
    }

}
