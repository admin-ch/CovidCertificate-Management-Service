package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

@Tag("CovidCertificatePersonNameDtoTest")
@DisplayName("Tests for the CovidCertificatePersonNameDto")
public class CovidCertificatePersonNameDtoTest {

    private final String validGivenName = "givenName";
    private final String validFamilyName = "familyName";

    private final int MAX_NAME_CHARS_LENGTH = 80;

    private void assertInvalidFamilyName(CovidCertificatePersonNameDto covidCertificatePersonNameDto) {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, covidCertificatePersonNameDto::validate);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getHttpStatus());
        assertEquals(459, exception.getError().getErrorCode());
        assertEquals("Invalid family name! Must not exceed 80 chars and/or not contain any invalid chars", exception.getError().getErrorMessage());
        assertEquals(INVALID_FAMILY_NAME, exception.getError());
    }

    private void assertInvalidMaher(CovidCertificatePersonNameDto covidCertificatePersonNameDto) {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, covidCertificatePersonNameDto::validate);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getHttpStatus());
        assertEquals(459, exception.getError().getErrorCode());
        assertEquals("Invalid family name! Must not exceed 80 chars and/or not contain any invalid chars", exception.getError().getErrorMessage());
        assertEquals(INVALID_FAMILY_NAME, exception.getError());
    }

    private void assertInvalidGivenName(CovidCertificatePersonNameDto covidCertificatePersonNameDto) {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, covidCertificatePersonNameDto::validate);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getHttpStatus());
        assertEquals(458, exception.getError().getErrorCode());
        assertEquals("Invalid given name! Must not exceed 80 chars and/or not contain any invalid chars", exception.getError().getErrorMessage());
        assertEquals(INVALID_GIVEN_NAME, exception.getError());
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
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(familyName, validGivenName);
            assertInvalidFamilyName(covidCertificatePersonNameDto);
        }

        @ParameterizedTest
        @ValueSource(strings = {"!", "@", "#", "\r", "\n", "\\", "$", "%", "¶", "*", "(", ")", "_", ":", "/", "+", "=", "|", "<", ">", "?", "{", "}", "[", "]", "~"})
        @DisplayName("Given 'familyName' contain an invalid character, when validated, it should throw an INVALID_FAMILY_NAME error (CreateCertificateException).")
        void validationTest2(String invalidChar) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(validFamilyName.concat(invalidChar), validGivenName);
            assertInvalidFamilyName(covidCertificatePersonNameDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH - 1, MAX_NAME_CHARS_LENGTH})
        @DisplayName("Given 'familyName' length <= " + MAX_NAME_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void validationTest5(int length) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto("f".repeat(length), validGivenName);
            assertDoesNotThrow(covidCertificatePersonNameDto::validate);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH + 1})
        @DisplayName("Given 'familyName' length > " + MAX_NAME_CHARS_LENGTH + ", when validated, it should throw an INVALID_FAMILY_NAME error (CreateCertificateException).")
        void validationTest6(int length) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto("f".repeat(length), validGivenName);
            assertInvalidFamilyName(covidCertificatePersonNameDto);
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
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(validFamilyName, givenName);
            assertInvalidGivenName(covidCertificatePersonNameDto);
        }

        @ParameterizedTest
        @ValueSource(strings = {"!", "@", "#", "\r", "\n", "\\", "$", "%", "¶", "*", "(", ")", "_", ":", "/", "+", "=", "|", "<", ">", "?", "{", "}", "[", "]", "~"})
        @DisplayName("Given 'givenName' contain an invalid character, when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest4(String invalidChar) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(validFamilyName, validGivenName.concat(invalidChar));
            assertInvalidGivenName(covidCertificatePersonNameDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH - 1, MAX_NAME_CHARS_LENGTH})
        @DisplayName("Given 'givenName' length <= " + MAX_NAME_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void validationTest7(int length) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(validFamilyName, "g".repeat(length));
            assertDoesNotThrow(covidCertificatePersonNameDto::validate);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_NAME_CHARS_LENGTH + 1})
        @DisplayName("Given 'givenName' length > " + MAX_NAME_CHARS_LENGTH + ", when validated, it should throw an INVALID_GIVEN_NAME error (CreateCertificateException).")
        void validationTest8(int length) {
            var covidCertificatePersonNameDto = new CovidCertificatePersonNameDto(validFamilyName, "g".repeat(length));
            assertInvalidGivenName(covidCertificatePersonNameDto);
        }
    }
}
