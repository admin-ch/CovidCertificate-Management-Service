package ch.admin.bag.covidcertificate.api.request.validator;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_CANT_BE_BEFORE;
import static ch.admin.bag.covidcertificate.api.Constants.MISSING_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Tag("LocalDateValidatorTest")
@DisplayName("Tests for the LocalDateValidator")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LocalDateValidatorTest {

    private final String LIMIT_DATE_STR = "20.09.1985";
    private final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final LocalDate LIMIT_DATE = LocalDate.parse(LIMIT_DATE_STR, LOCAL_DATE_FORMAT);

    public static Stream<Arguments> provideTestParameters() {
        DateTimeFormatter localDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return Stream.of(
                Arguments.of(LocalDate.parse("20.09.1985", localDateFormat), null, "date"),
                Arguments.of(null, LocalDate.parse("20.09.1985", localDateFormat), "limitDate"),
                Arguments.of(null, null, "date")
        );
    }

    private void assertMissingPropertyError(String text, CreateCertificateException exception) {
        // MISSING_PROPERTY
        assertEquals(MISSING_PROPERTY.getHttpStatus(), exception.getError().getHttpStatus());
        assertEquals(MISSING_PROPERTY.getErrorCode(), exception.getError().getErrorCode());
        assertEquals(String.format(MISSING_PROPERTY.getErrorMessage(), text), exception.getError().getErrorMessage());
    }

    private void assertDateCantBeBeforeError(String text, CreateCertificateException exception) {
        // DATE_CANT_BE_BEFORE
        assertEquals(DATE_CANT_BE_BEFORE.getHttpStatus(), exception.getError().getHttpStatus());
        assertEquals(DATE_CANT_BE_BEFORE.getErrorCode(), exception.getError().getErrorCode());
        assertEquals(String.format(DATE_CANT_BE_BEFORE.getErrorMessage(), text), exception.getError().getErrorMessage());
    }

    private void assertDateCantBeAfterError(String text, CreateCertificateException exception) {
        // DATE_CANT_BE_AFTER
        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getHttpStatus());
        assertEquals(495, exception.getError().getErrorCode());
        assertEquals(String.format("Date can't be after %s!", text), exception.getError().getErrorMessage());
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotMissing")
    @DisplayName("validateDateIsNotMissing")
    class ValidateDateIsNotMissingTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "myPropertyName"})
        @DisplayName("Given date is null, when validateDateIsNotMissing is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1(String propertyName) {
            LocalDate date = null;
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotMissing(date, propertyName));
            assertMissingPropertyError(propertyName, exception);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "myPropertyName"})
        @DisplayName("Given date is not null, when validateDateIsNotMissing is called, it should not throw an exception.")
        void test2(String propertyName) {
            LocalDate date = LocalDate.now();
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotMissing(date, propertyName));
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotInThePast")
    @DisplayName("validateDateIsNotInThePast")
    class ValidateDateIsNotInThePastTests {

        @Test
        @DisplayName("Given date is null, when validateDateIsNotInThePast is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1() {
            LocalDate date = null;
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotInThePast(date, "date"));
            assertMissingPropertyError("date", exception);
        }

        @Test
        @DisplayName("Given date is now, when validateDateIsNotInThePast is called, it should not throw an exception.")
        void test2() {
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotInThePast(LocalDate.now(), "date"));
        }

        @Test
        @DisplayName("Given date is now minus 1 day, when validateDateIsNotInThePast is called, it should not throw an exception.")
        void test3() {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotInThePast(LocalDate.now().minusDays(1), "date"));
            assertDateCantBeBeforeError("now", exception);
        }

        @Test
        @DisplayName("Given date is now plus 1 day, when validateDateIsNotInThePast is called, it should throw a DATE_CANT_BE_BEFORE error (CreateCertificateException).")
        void test4() {
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotInThePast(LocalDate.now().plusDays(1), "date"));
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotInTheFuture")
    @DisplayName("validateDateIsNotInTheFuture")
    class ValidateDateIsNotInTheFutureTests {

        @Test
        @DisplayName("Given date is null, when validateDateIsNotInTheFuture is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1() {
            LocalDate date = null;
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotInTheFuture(date, "date"));
            assertMissingPropertyError("date", exception);
        }

        @Test
        @DisplayName("Given date is now, when validateDateIsNotInTheFuture is called, it should not throw an exception.")
        void test2() {
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotInTheFuture(LocalDate.now(), "date"));
        }

        @Test
        @DisplayName("Given date is now minus 1 day, when validateDateIsNotInTheFuture is called, it should not throw an exception.")
        void test3() {
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotInTheFuture(LocalDate.now().minusDays(1), "date"));
        }

        @Test
        @DisplayName("Given date is now plus 1 day, when validateDateIsNotInTheFuture is called, it should throw a DATE_CANT_BE_AFTER error (CreateCertificateException).")
        void test4() {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotInTheFuture(LocalDate.now().plusDays(1), "date"));
            assertDateCantBeAfterError("now", exception);
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotBeforeLimitDate")
    @DisplayName("validateDateIsNotBeforeLimitDate")
    class ValidateDateIsNotBeforeLimitDateTests {

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidatorTest#provideTestParameters")
        @DisplayName("Given limit-date and date, when validateDateIsNotBeforeLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> MethodSource")
        void test1(LocalDate limitDate, LocalDate date, String text) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotBeforeLimitDate(date, "date", limitDate));
            assertMissingPropertyError(text, exception);
        }

        @ParameterizedTest
        @CsvSource({"20.09.1985,null,date", "null,20.09.1985,limitDate", "null,null,date"})
        @DisplayName("Given limit-date and date, when validateDateIsNotBeforeLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> CsvSource")
        void test2(String limitDateStr, String dateStr, String text) {
            LocalDate limitDate = "null".equals(limitDateStr) ? null : LocalDate.parse(limitDateStr, LOCAL_DATE_FORMAT);
            LocalDate date = "null".equals(dateStr) ? null : LocalDate.parse(dateStr, LOCAL_DATE_FORMAT);
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotBeforeLimitDate(date, "date", limitDate));
            assertMissingPropertyError(text, exception);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is equal of greater than limit date, when validateDateIsNotBeforeLimitDate is called, it should not throw an exception.")
        void test3(int positiveDayOffset) {
            LocalDate date = LIMIT_DATE.plusDays(positiveDayOffset);
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotBeforeLimitDate(date, "date", LIMIT_DATE));
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is smaller than limit date, when validateDateIsNotBeforeLimitDate is called, it should throw a DATE_CANT_BE_BEFORE error (CreateCertificateException).")
        void test4(int negativeDayOffset) {
            LocalDate date = LIMIT_DATE.minusDays(negativeDayOffset);
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotBeforeLimitDate(date, "date", LIMIT_DATE));
            assertDateCantBeBeforeError(LIMIT_DATE.format(LOCAL_DATE_FORMAT), exception);
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotAfterLimitDate")
    @DisplayName("validateDateIsNotAfterLimitDate")
    class ValidateDateIsNotAfterLimitDateTests {

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidatorTest#provideTestParameters")
        @DisplayName("Given limit-date and date, when validateDateIsNotAfterLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> MethodSource")
        void test1(LocalDate limitDate, LocalDate date, String text) {
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotAfterLimitDate(date, "date", limitDate));
            assertMissingPropertyError(text, exception);
        }

        @ParameterizedTest
        @CsvSource({"20.09.1985,null,date", "null,20.09.1985,limitDate", "null,null,date"})
        @DisplayName("Given limit-date and date, when validateDateIsNotAfterLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> CsvSource")
        void test2(String limitDateStr, String dateStr, String text) {
            LocalDate limitDate = "null".equals(limitDateStr) ? null : LocalDate.parse(limitDateStr, LOCAL_DATE_FORMAT);
            LocalDate date = "null".equals(dateStr) ? null : LocalDate.parse(dateStr, LOCAL_DATE_FORMAT);
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotAfterLimitDate(date, "date", limitDate));
            assertMissingPropertyError(text, exception);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is equal of greater than limit date, when validateDateIsNotAfterLimitDate is called, it should throw a DATE_CANT_BE_AFTER error (CreateCertificateException).")
        void test3(int positiveDayOffset) {
            LocalDate date = LIMIT_DATE.plusDays(positiveDayOffset);
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> LocalDateValidator.validateDateIsNotAfterLimitDate(date, "date", LIMIT_DATE));
            assertDateCantBeAfterError(LIMIT_DATE.format(LOCAL_DATE_FORMAT), exception);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is smaller than limit date, when validateDateIsNotAfterLimitDate is called, it should throw a DATE_CANT_BE_BEFORE error (CreateCertificateException).")
        void test4(int negativeDayOffset) {
            LocalDate date = LIMIT_DATE.minusDays(negativeDayOffset);
            assertDoesNotThrow(() -> LocalDateValidator.validateDateIsNotAfterLimitDate(date, "date", LIMIT_DATE));
        }
    }
}