package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;


@Tag("CovidCertificatePersonNameDtoTest")
@DisplayName("Tests for the CovidCertificatePersonNameDto")
public class CovidCertificatePersonNameDtoTest {

    private final String validGivenName = "givenName";
    private final String validFamilyName = "familyName";

    private final int MAX_NAME_CHARS_LENGTH = 80;

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

    @Nested
    @Tag("CovidCertificatePersonNameDtoTest.familyName")
    @DisplayName("'familyName' can't contain an invalid character and length must be <= " + MAX_NAME_CHARS_LENGTH + ".")
    class FamilyNameTests {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'familyName' is blank, when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest1(String familyName) {
            var testee = new CovidCertificatePersonNameDto(familyName, validGivenName);
            var violations = validator.validateProperty(testee, "familyName");
            if (familyName == null) {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Family name must not be null")));
            } else if (familyName.length() > 80) {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid family name! Must not exceed 80 chars")));
            } else {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid family name! Must not contain any invalid chars")));
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {"!", "@", "#", "\\", "$", "%", "¶", "*", "(", ")", "_", ":", "/", "+", "=", "|", "<", ">", "?", "{", "}", "[", "]", "~"})
        @DisplayName("Given 'familyName' contain an invalid character, when validated, it should throw an INVALID_FAMILY_NAME error (CreateCertificateException).")
        void validationTest2(String invalidChar) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName.concat(invalidChar), validGivenName);
            var violations = validator.validateProperty(testee, "familyName");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid family name! Must not contain any invalid chars")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH - 1, MAX_NAME_CHARS_LENGTH})
        @DisplayName("Given 'familyName' length <= " + MAX_NAME_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void validationTest5(int length) {
            var testee = new CovidCertificatePersonNameDto("f".repeat(length), validGivenName);
            var violations = validator.validateProperty(testee, "familyName");
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH + 1})
        @DisplayName("Given 'familyName' length > " + MAX_NAME_CHARS_LENGTH + ", when validated, it should throw an INVALID_FAMILY_NAME error (CreateCertificateException).")
        void validationTest6(int length) {
            var testee = new CovidCertificatePersonNameDto("f".repeat(length), validGivenName);
            var violations = validator.validateProperty(testee, "familyName");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid family name! Must not exceed " + MAX_STRING_LENGTH + " chars")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Hans Ueli", " Hans Ueli ", " Hans\tUeli"})
        @DisplayName("Given 'familyName' contain an none-breaking characters, tabs or is untrimmed, when validated, it sanitize the 'familyName'.")
        void validationTest7(String unsanitizedName) {
            var testee = new CovidCertificatePersonNameDto(unsanitizedName, validGivenName);
            var violations = validator.validateProperty(testee, "givenName");
            assertTrue(violations.isEmpty());
            assertEquals("Hans Ueli", testee.getFamilyName());
        }
    }

    @Nested
    @Tag("CovidCertificatePersonNameDtoTest.givenName")
    @DisplayName("'givenName' can't contain an invalid character and length must be <= " + MAX_NAME_CHARS_LENGTH + ".")
    class GivenNameTests {
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'givenName' is blank, when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest3(String givenName) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName, givenName);
            var violations = validator.validateProperty(testee, "givenName");
            if (givenName == null) {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Given name must not be null")));
            } else if (givenName.length() > 80) {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid given name! Must not exceed 80 chars")));
            } else {
                assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid given name! Must not contain any invalid chars")));
            }

        }

        @ParameterizedTest
        @ValueSource(strings = {"!", "@", "#", "\\", "$", "%", "¶", "*", "(", ")", "_", ":", "/", "+", "=", "|", "<", ">", "?", "{", "}", "[", "]", "~"})
        @DisplayName("Given 'givenName' contain an invalid character, when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest4(String invalidChar) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName, validGivenName.concat(invalidChar));
            var violations = validator.validateProperty(testee, "givenName");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid given name! Must not contain any invalid chars")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH - 1, MAX_NAME_CHARS_LENGTH})
        @DisplayName("Given 'givenName' length <= " + MAX_NAME_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void validationTest7(int length) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName, "g".repeat(length));
            var violations = validator.validateProperty(testee, "givenName");
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH + 1})
        @DisplayName("Given 'givenName' length > " + MAX_NAME_CHARS_LENGTH + ", when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest8(int length) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName, "g".repeat(length));
            var violations = validator.validateProperty(testee, "givenName");
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid given name! Must not exceed " + MAX_STRING_LENGTH + " chars")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"Franz Mann", " Franz Mann ", " Franz\tMann"})
        @DisplayName("Given 'givenName' contain an none-breaking characters, tabs or is untrimmed, when validated, it sanitize the 'givenName'.")
        void validationTest9(String unsanitizedName) {
            var testee = new CovidCertificatePersonNameDto(validFamilyName, unsanitizedName);
            var violations = validator.validateProperty(testee, "givenName");
            assertTrue(violations.isEmpty());
            assertEquals("Franz Mann", testee.getGivenName());
        }
    }
}
