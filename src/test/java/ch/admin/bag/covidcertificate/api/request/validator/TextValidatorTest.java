package ch.admin.bag.covidcertificate.api.request.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


@Tag("TextValidatorTest")
@DisplayName("Tests for the TextValidator")
class TextValidatorTest {

    private final int MAX_TEXT_LENGTH = 80;

    @Nested
    @Tag("TextValidator.validateTextIsNotNullAndNotEmpty")
    @DisplayName("validateTextIsNotNullAndNotEmpty")
    class ValidateTextIsNotNullAndNotEmptyTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Given text is null or empty, when validateTextIsNotNullAndNotEmpty is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1(String text) {
            assertFalse(TextValidator.validateTextIsNotNullAndNotEmpty(text));
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text", "   "})
        @DisplayName("Given text is not empty or blank (e.g. 3 spaces), when validateTextIsNotNullAndNotEmpty is called, it should not throw an exception")
        void test2(String text) {
            assertTrue(TextValidator.validateTextIsNotNullAndNotEmpty(text));
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
            assertFalse(TextValidator.validateTextIsNotBlank(text));
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text"})
        @DisplayName("Given text is not empty, when validateTextIsNotBlank is called, it should not throw an exception")
        void test2(String text) {
            assertTrue(TextValidator.validateTextIsNotBlank(text));
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
            assertFalse(TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(text, MAX_TEXT_LENGTH));
        }

        @ParameterizedTest
        @ValueSource(strings = {"random-text", "random text"})
        @DisplayName("Given text is not empty and length is smaller than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should not throw an exception")
        void test2(String text) {
            assertTrue(TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(text, MAX_TEXT_LENGTH));
        }


        @ParameterizedTest
        @ValueSource(ints = {MAX_TEXT_LENGTH - 1, MAX_TEXT_LENGTH})
        @DisplayName("Given text is not blank and length is smaller or equal than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should not throw an exception")
        void test3(int length) {
            assertTrue(TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength("a".repeat(length), MAX_TEXT_LENGTH));
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_TEXT_LENGTH + 1,})
        @DisplayName("Given text is not blank and length is bigger than " + MAX_TEXT_LENGTH + ", when validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test4(int length) {
            assertFalse(TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength("a".repeat(length), MAX_TEXT_LENGTH));
        }
    }
}