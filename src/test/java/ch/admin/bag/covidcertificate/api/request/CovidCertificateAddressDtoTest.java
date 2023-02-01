package ch.admin.bag.covidcertificate.api.request;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.ReflectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CovidCertificateAddressDtoTest")
@DisplayName("Tests for the CovidCertificateAddressDto")
class CovidCertificateAddressDtoTest {

    private final int MAX_STREETANDNR_CHARS_LENGTH = 128;
    private final int MAX_CITY_CHARS_LENGTH = 128;
    private final int MIN_ZIPCODE_VALUE = 1000;
    private final int MAX_ZIPCODE_VALUE = 9999;

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeEach
    public void createValidator() {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
            validator = validatorFactory.getValidator();
        }
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
        validatorFactory = null;
    }

    @Test
    @DisplayName("Given all the parameters for CovidCertificateAddressDto are valid, when validated, it should not throw an exception.")
    void CovidCertificateAddressDtoTest1() {
        var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", "BE");
        Set<ConstraintViolation<CovidCertificateAddressDto>> violations = validator.validateProperty(testee, "streetAndNr");
        assertTrue(violations.isEmpty());
        violations = validator.validateProperty(testee, "city");
        assertTrue(violations.isEmpty());

        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isZipCodeInRange").orElseThrow();

        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isZipCodeInRange());
        assertTrue(violations.isEmpty());

        methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isAllowedSender").orElseThrow();

        violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isAllowedSender());
        assertTrue(violations.isEmpty());

    }

    @Nested
    @Tag("CovidCertificateAddressDto.cantonCodeSender")
    @DisplayName("'cantonCodeSender' must be a valid canton code or MI.")
    class CantonCodeSenderTests {

        @ParameterizedTest
        @ValueSource(strings = {"AG", "AI", "AR", "BE", "BL", "BS", "FR", "GE", "GL", "GR", "JU", "LU", "NE", "NW", "OW", "SG", "SH", "SO", "SZ", "TG", "TI", "UR", "VD", "VS", "ZG", "ZH", "MI"})
        @DisplayName("Given 'cantonCodeSender' is within the value set, when validated, it should not throw an exception.")
        void cantonCodeSenderTest1(String cantonCodeSender) {
            var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", cantonCodeSender);
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isAllowedSender").orElseThrow();
            var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isAllowedSender());
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "ZZ"})
        @DisplayName("Given 'cantonCodeSender' is blank or not in the value set, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cantonCodeSenderTest2(String cantonCodeSender) {
            var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", cantonCodeSender);
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isAllowedSender").orElseThrow();
            var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isAllowedSender());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));

        }
    }

    @Nested
    @Tag("CovidCertificateAddressDto.streetAndNr")
    @DisplayName("'streetAndNr' can't be blank and can't exceed " + MAX_STREETANDNR_CHARS_LENGTH + " characters.")
    class StreetAndNumberTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'streetAndNr' is blank, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void streetAndNumberTest1(String streetAndNr) {
            var testee = new CovidCertificateAddressDto(streetAndNr, MIN_ZIPCODE_VALUE, "city", "BE");
            var violations = validator.validateProperty(testee, "streetAndNr");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_STREETANDNR_CHARS_LENGTH - 1, MAX_STREETANDNR_CHARS_LENGTH})
        @DisplayName("Given 'streetAndNr' length <= " + MAX_STREETANDNR_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void streetAndNumberTest2(int streetAndNrLength) {
            String streetAndNumber = RandomStringUtils.random(streetAndNrLength, true, true);
            var testee = new CovidCertificateAddressDto(streetAndNumber, MIN_ZIPCODE_VALUE, "city", "BE");
            var violations = validator.validateProperty(testee, "streetAndNr");
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Given 'streetAndNr' length > " + MAX_STREETANDNR_CHARS_LENGTH + " characters, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void streetAndNumberTest3() {
            String streetAndNumber = RandomStringUtils.random(MAX_STREETANDNR_CHARS_LENGTH + 1, true, true);
            var testee = new CovidCertificateAddressDto(streetAndNumber, MIN_ZIPCODE_VALUE, "city", "BE");
            var violations = validator.validateProperty(testee, "streetAndNr");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));
        }
    }

    @Nested
    @Tag("CovidCertificateAddressDto.zipCode")
    @DisplayName("'zipCode' must be contained in the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval.")
    class ZipCodeTests {

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE - 1, MAX_ZIPCODE_VALUE + 1})
        @DisplayName("Given 'zipCode' is outside of the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void zipCodeTest1(int zipCode) {
            var testee = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isZipCodeInRange").orElseThrow();
            var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isZipCodeInRange());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE, MAX_ZIPCODE_VALUE})
        @DisplayName("Given 'zipCode' is equal to the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval limits, when validated, it should not throw an exception.")
        void zipCodeTest2(int zipCode) {
            var testee = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isZipCodeInRange").orElseThrow();
            var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isZipCodeInRange());
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE + 1, MAX_ZIPCODE_VALUE - 1})
        @DisplayName("Given 'zipCode' is contained in the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval, when validated, it should not throw an exception.")
        void zipCodeTest3(int zipCode) {
            var testee = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isZipCodeInRange").orElseThrow();
            var violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isZipCodeInRange());
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    @Tag("CovidCertificateAddressDtoTest.city")
    @DisplayName("'city' can't be blank and can't exceed " + MAX_CITY_CHARS_LENGTH + " characters length.")
    class CityTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'city' is blank, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cityTest1(String city) {
            var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            var violations = validator.validateProperty(testee, "city");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_CITY_CHARS_LENGTH - 1, MAX_CITY_CHARS_LENGTH})
        @DisplayName("Given 'city' length <= " + MAX_CITY_CHARS_LENGTH + " characters, when validated, it should not throw an exception.")
        void cityTest2(int cityLength) {
            String city = RandomStringUtils.random(cityLength, true, true);
            var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            var violations = validator.validateProperty(testee, "city");
            assertTrue(violations.isEmpty());
        }

        @Test
        @DisplayName("Given 'city' length > " + MAX_STREETANDNR_CHARS_LENGTH + " characters, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cityTest3() {
            String city = RandomStringUtils.random(MAX_STREETANDNR_CHARS_LENGTH + 1, true, true);
            var testee = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            var violations = validator.validateProperty(testee, "city");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Paper-based delivery requires a valid address.")));
        }
    }
}