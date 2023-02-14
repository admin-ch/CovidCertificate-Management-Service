package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static ch.admin.bag.covidcertificate.TestModelProvider.getCovidCertificateAddressDto;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_PRINT_FOR_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.NO_TEST_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final TestCertificateDataDto dataDto = mock(TestCertificateDataDto.class);

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
    public void testNoTestData() {
        String language = "de";
        TestCertificateCreateDto testee = new TestCertificateCreateDto(
                personDto,
                null,
                language,
                null,
                null,
                SystemSource.WebUI
       );
        var violations = validator.validateProperty(testee, "testInfo");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No test data was specified")));

        testee = new TestCertificateCreateDto(
                personDto,
                List.of(),
                language,
                null,
                null,
                SystemSource.WebUI
        );
        violations = validator.validateProperty(testee, "testInfo");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("No test data was specified")));


        testee = new TestCertificateCreateDto(
                personDto,
                List.of(),
                language,
                getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidPrint").orElseThrow();

        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidPrint());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Print is not available for test certificates")));

        testee = new TestCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language,
                null,
                null,
                SystemSource.WebUI
        );

        violations = validator.validateProperty(testee, "testInfo");
        assertTrue(violations.isEmpty());
        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidPrint").orElseThrow();
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidPrint());
        assertTrue(violations.isEmpty());

    }
}
