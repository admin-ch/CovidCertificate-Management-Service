package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;
import static org.junit.jupiter.api.Assertions.*;

public class RecoveryCertificateDataDtoTest {

    private final LocalDate dateOfFirstPositiveTestResult = LocalDate.of(2021, Month.MAY, 2);
    private final String countryOfTest = "CH";

    @Test
    public void testInvalidDateOfFirstPositiveTestResult() {
        RecoveryCertificateDataDto testee = new RecoveryCertificateDataDto(
                null,
                countryOfTest
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT, exception.getError());

        testee = new RecoveryCertificateDataDto(
                LocalDate.now().plusDays(1),
                countryOfTest
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT, exception.getError());

        testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidCountryOfTest() {
        RecoveryCertificateDataDto testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());

        testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        assertDoesNotThrow(testee::validate);
    }
}
