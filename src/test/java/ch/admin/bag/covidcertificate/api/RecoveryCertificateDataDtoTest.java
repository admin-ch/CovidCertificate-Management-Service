package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;
import static org.junit.jupiter.api.Assertions.*;

public class RecoveryCertificateDataDtoTest {

    private final LocalDate dateOfFirstPositiveTestResult = LocalDate.of(2021, Month.MAY, 2);
    private final String countryOfTest = "CH";

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeClass
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void validationSucceeds_ifDateOfFirstPositiveTestResult_isInThePast() {
        final var testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDteOfFirstPositiveTestResult").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDteOfFirstPositiveTestResult());
        assertTrue(violations.isEmpty());

    }

    @Test
    public void validationThrowsCreateCertificateException_ifDateOfFirstPositiveTestResult_isNull() {
        final var testee = new RecoveryCertificateDataDto(
                null,
                countryOfTest
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDteOfFirstPositiveTestResult").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDteOfFirstPositiveTestResult());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid date of first positive test result")));

    }

    @Test
    public void validationThrowsCreateCertificateException_ifDateOfFirstPositiveTestResult_isInTheFuture() {
        final var testee = new RecoveryCertificateDataDto(
                LocalDate.now().plusDays(1),
                countryOfTest
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDteOfFirstPositiveTestResult").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDteOfFirstPositiveTestResult());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid date of first positive test result")));
    }

    @Test
    public void validationThrowsCreateCertificateException_ifCountryOfTest_isNull() {
        final var testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                null
        );
        var violations = validator.validateProperty(testee, "countryOfTest");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid country of test")));
    }

    @Test
    public void validationSucceeds_ifCountryOfTest_isNotNull() {
        final var testee = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        var violations = validator.validateProperty(testee, "countryOfTest");
        assertTrue(violations.isEmpty());
    }
}
