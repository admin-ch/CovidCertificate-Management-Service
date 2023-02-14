package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCertificateDataDtoTest {

    private final String manufacturerCode = "manufacturerCode";
    private final String typeCode = "";
    private final ZonedDateTime sampleDateTime = ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 4, 16, 25), SWISS_TIMEZONE);
    private final String testingCentreOrFacility = "testCenter";
    private final String memberStateOfTest = "CH";

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
    public void testInvalidSampleOrResultDateTime() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                null,
                testingCentreOrFacility,
                memberStateOfTest
        );
        var violations = validator.validateProperty(testee, "sampleDateTime");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid sample date time! Sample date must be before current date time")));


        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                ZonedDateTime.now().plusDays(1),
                testingCentreOrFacility,
                memberStateOfTest
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isSampleDateTimeValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isSampleDateTimeValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid sample date time! Sample date must be before current date time")));

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                ZonedDateTime.now().plusMinutes(5),
                testingCentreOrFacility,
                memberStateOfTest
        );
        violations = validator.validateProperty(testee, "sampleDateTime");
        assertTrue(violations.isEmpty());
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isSampleDateTimeValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isSampleDateTimeValid());
        assertTrue(violations.isEmpty());

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        violations = validator.validateProperty(testee, "sampleDateTime");
        assertTrue(violations.isEmpty());
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isSampleDateTimeValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isSampleDateTimeValid());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidTestCenter() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                null,
                memberStateOfTest
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isTestCenterValid").orElseThrow();
        var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isTestCenterValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid testing center or facility")));

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                "",
                memberStateOfTest
        );
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isTestCenterValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isTestCenterValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid testing center or facility")));

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                "lslksiueiufhflsleifhgoskeiusfdsafdrfhffdsafdsafdsafdsafvasfdsafgdsagfdadsafdsafdsa",
                memberStateOfTest
        );
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isTestCenterValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isTestCenterValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid testing center or facility")));

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isTestCenterValid").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isTestCenterValid());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidMemberStateOfTest() {
        TestCertificateDataDto testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                null
        );
        var violations = validator.validateProperty(testee, "memberStateOfTest");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid member state of test")));

        testee = new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                sampleDateTime,
                testingCentreOrFacility,
                memberStateOfTest
        );
        violations = validator.validateProperty(testee, "memberStateOfTest");
        assertTrue(violations.isEmpty());
    }
}
