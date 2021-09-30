package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecoveryCertificateDataDtoTest {

    private final LocalDate dateOfFirstPositiveTestResult = LocalDate.of(2021, Month.MAY, 2);
    private final String countryOfTest = "CH";

    @Test
    public void validationSucceeds_ifDateOfFirstPositiveTestResult_isInThePast() {
        final RecoveryCertificateDataDto testDto = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        assertDoesNotThrow(() -> testDto.validate(SystemSource.WebUI));
    }

    @Test
    public void validationThrowsCreateCertificateException_ifDateOfFirstPositiveTestResult_isNull() {
        final RecoveryCertificateDataDto testDto = new RecoveryCertificateDataDto(
                null,
                countryOfTest
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> testDto.validate(
                SystemSource.WebUI));
        assertEquals(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT, exception.getError());
    }

    @Test
    public void validationThrowsCreateCertificateException_ifDateOfFirstPositiveTestResult_isInTheFuture() {
        final RecoveryCertificateDataDto testDto = new RecoveryCertificateDataDto(
                LocalDate.now().plusDays(1),
                countryOfTest
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> testDto.validate(
                SystemSource.WebUI));
        assertEquals(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT, exception.getError());
    }

    @Test
    public void validationThrowsCreateCertificateException_ifCountryOfTest_isNull() {
        final RecoveryCertificateDataDto testDto = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                                                            () -> testDto.validate(SystemSource.WebUI));
        assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
    }

    @Test
    public void validationSucceeds_ifCountryOfTest_isNotNull() {
        final RecoveryCertificateDataDto testDto = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        assertDoesNotThrow(() -> testDto.validate(SystemSource.WebUI));
    }
}
