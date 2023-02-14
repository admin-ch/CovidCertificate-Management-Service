package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.request.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final String language = "de";
    private final CovidCertificateAddressDto addressDto = new CovidCertificateAddressDto("street", 1000, "city", "BE");

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

    private static class CertificateCreateDtoIml extends CertificateCreateDto {
        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                SystemSource systemSource
        ) {
            super(personData, language, null, null, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address,
                SystemSource systemSource
        ) {
            super(personData, language, address, null, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                String inAppDeliveryCode,
                SystemSource systemSource
        ) {
            super(personData, language, null, inAppDeliveryCode, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address,
                String inAppDeliveryCode,
                SystemSource systemSource
        ) {
            super(personData, language, address, inAppDeliveryCode, systemSource);
        }
    }

    @Test
    public void throwsCertificateCreateException__onNoPersonData() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                null,
                language,
                addressDto,
                SystemSource.WebUI
        );
        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.validateProperty(testee, "personData");
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("personData must not be null")));

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto,
                SystemSource.WebUI
        );

        violations = validator.validateProperty(testee, "personData");
        assertTrue(violations.isEmpty());

    }

    @Test
    public void throwsCertificateCreateException__onInvalidLanguage() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                null,
                addressDto,
                SystemSource.WebUI
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isLanguageValid").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isLanguageValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The given language does not match any of the supported languages: de, it, fr, rm!")));

        testee = new CertificateCreateDtoIml(
                personDto,
                "en",
                addressDto,
                SystemSource.WebUI
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isLanguageValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("The given language does not match any of the supported languages: de, it, fr, rm!")));

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto,
                SystemSource.WebUI
        );
        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isLanguageValid());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void throwsException__ifAddressAndInAppDeliveryCodeArePassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto, "de", addressDto, RandomStringUtils.randomAlphanumeric(9), SystemSource.WebUI);

        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDuplicateDeliveryMethod").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDuplicateDeliveryMethod());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Delivery method can either be InApp or print, but not both.")));
    }

    @Test
    public void throwsException__ifInvalidInAppDeliveryCode() {
        // test not alphanumeric
        var testee = new CertificateCreateDtoIml(
                personDto, "de", RandomStringUtils.random(9), SystemSource.WebUI);

        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isValidAppCode").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isValidAppCode());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("App code is in an invalid format.")));
    }

    @Test
    public void validatesSuccessfully__ifAddressOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", addressDto, SystemSource.WebUI);
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDuplicateDeliveryMethod").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDuplicateDeliveryMethod());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validatesSuccessfully__ifInAppDeliveryCodeOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto, "de", RandomStringUtils.randomAlphanumeric(9), SystemSource.WebUI);
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDuplicateDeliveryMethod").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDuplicateDeliveryMethod());
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validatesSuccessfully__ifNoDeliveryIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", SystemSource.WebUI);
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDuplicateDeliveryMethod").orElseThrow();

        Set<ConstraintViolation<CertificateCreateDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDuplicateDeliveryMethod());
        assertTrue(violations.isEmpty());
    }
}
