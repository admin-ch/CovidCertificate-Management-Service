package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CovidCertificatePersonDtoTest {

    private final CovidCertificatePersonNameDto personNameDto = mock(CovidCertificatePersonNameDto.class);
    private final LocalDate dateOfBirth = LocalDate.of(1989, Month.JANUARY, 17);

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
    public void testNoPersonData() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                null,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
        );
        var violations = validator.validateProperty(testee, "name");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No person data was specified")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
        );
        violations = validator.validateProperty(testee, "name");
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidDateOfBirth() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                null
        );
        var violations = validator.validateProperty(testee, "dateOfBirth");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Date of birth must not be null")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                MIN_DATE_OF_BIRTH.minusDays(1).format(LOCAL_DATE_FORMAT)
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDateOfBirth").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirth());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid dateOfBirth! Must be younger than 1900-01-01")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                MAX_DATE_OF_BIRTH.plusDays(1).format(LOCAL_DATE_FORMAT)
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirth());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid dateOfBirth! Must be younger than 1900-01-01")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                dateOfBirth.format(LOCAL_DATE_FORMAT)
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirth());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidDateOfBirth_oneDayInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusDays(1).format(LOCAL_DATE_FORMAT)
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDateOfBirthInFuture").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid dateOfBirth! Date cannot be in the future")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(LOCAL_DATE_FORMAT)
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.isEmpty());

    }

    @Test
    public void testInvalidDateOfBirth_oneMonthInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDateOfBirthInFuture").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid dateOfBirth! Date cannot be in the future")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidDateOfBirth_oneYearInFuture() {
        CovidCertificatePersonDto testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("yyyy"))
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidDateOfBirthInFuture").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid dateOfBirth! Date cannot be in the future")));

        testee = new CovidCertificatePersonDto(
                personNameDto,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidDateOfBirthInFuture());
        assertTrue(violations.isEmpty());
    }

}
