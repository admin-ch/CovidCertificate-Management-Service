package ch.admin.bag.covidcertificate.api.request.validator;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static ch.admin.bag.covidcertificate.api.Constants.MISSING_PROPERTY;
import static ch.admin.bag.covidcertificate.api.Constants.TEXT_INVALID_LENGTH;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Tag("TextValidatorTest")
@DisplayName("Tests for the TextValidator")
class TextValidatorTest {

    private final int MAX_TEXT_LENGTH = 80;

    private void assertMissingPropertyError(String text, CreateCertificateException exception) {
        // MISSING_PROPERTY
        assertEquals(MISSING_PROPERTY.getHttpStatus(), exception.getError().getHttpStatus());
        assertEquals(MISSING_PROPERTY.getErrorCode(), exception.getError().getErrorCode());
        assertEquals(String.format(MISSING_PROPERTY.getErrorMessage(), text), exception.getError().getErrorMessage());
    }

    private void assertTextInvalidLengthError(String text, int lengthLimit, CreateCertificateException exception) {
        // TEXT_INVALID_LENGTH
        assertEquals(TEXT_INVALID_LENGTH.getHttpStatus(), exception.getError().getHttpStatus());
        assertEquals(TEXT_INVALID_LENGTH.getErrorCode(), exception.getError().getErrorCode());
        assertEquals(String.format(TEXT_INVALID_LENGTH.getErrorMessage(), text, lengthLimit), exception.getError().getErrorMessage());
    }

    @Nested
    @Tag("TextValidator.validateTextIsNotNullAndNotEmpty")
    @DisplayName("validateTextIsNotNullAndNotEmpty")
    class ValidateTextIsNotNullAndNotEmptyTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Given text is null or empty, when validateTextIsNotNullAndNotEmpty is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1(String text) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> TextValidator.validateTextIsNotNullAndNotEmpty(text, "text"));
            assertMissingPropertyError("text", exception);
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text", "   "})
        @DisplayName("Given text is not empty or blank (e.g. 3 spaces), when validateTextIsNotNullAndNotEmpty is called, it should not throw an exception")
        void test2(String text) {
            assertDoesNotThrow(() -> TextValidator.validateTextIsNotNullAndNotEmpty(text, "text"));
        }
    }

    @Nested
    @Tag("TextValidator.validateTextIsNotBlank")
    @DisplayName("validateTextIsNotBlank")
    class ValidateTextIsNotBlankTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Given text is null, empty or blank (e.g. 3 spaces), when validateTextIsNotBlank is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1(String text) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> TextValidator.validateTextIsNotBlank(text, "text"));
            assertMissingPropertyError("text", exception);
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text"})
        @DisplayName("Given text is not empty, when validateTextIsNotBlank is called, it should not throw an exception")
        void test2(String text) {
            assertDoesNotThrow(() -> TextValidator.validateTextIsNotBlank(text, "text"));
        }
    }

    @Nested
    @Tag("TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength")
    @DisplayName("validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength")
    class ValidateTextIsNotBlankAndLengthIsNotBiggerThanMaxLengthTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Given text is null, empty or blank (e.g. 3 spaces), when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1(String text) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(text, "text", MAX_TEXT_LENGTH));
            assertMissingPropertyError("text", exception);
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text"})
        @DisplayName("Given text is not empty and length is smaller than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should not throw an exception")
        void test2(String text) {
            assertDoesNotThrow(() -> TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(text, "text", MAX_TEXT_LENGTH));
        }


        @ParameterizedTest
        @ValueSource(ints = {MAX_TEXT_LENGTH - 1, MAX_TEXT_LENGTH})
        @DisplayName("Given text is not blank and length is smaller or equal than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should not throw an exception")
        void test3(int length) {
            assertDoesNotThrow(() -> TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength("a".repeat(length), "text", MAX_TEXT_LENGTH));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_TEXT_LENGTH + 1,})
        @DisplayName("Given text is not blank and length is bigger than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test4(int length) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength("a".repeat(length), "text", MAX_TEXT_LENGTH));
            assertTextInvalidLengthError("text", MAX_TEXT_LENGTH, exception);
        }
    }
}