package ch.admin.bag.covidcertificate.api.request.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


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
                Arguments.of(LocalDate.parse("20.09.1985", localDateFormat), null),
                Arguments.of(null, LocalDate.parse("20.09.1985", localDateFormat)),
                Arguments.of(null, null)
        );
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotInTheFuture")
    @DisplayName("validateDateIsNotInTheFuture")
    class ValidateDateIsNotInTheFutureTests {

        @Test
        @DisplayName("Given date is null, when validateDateIsNotInTheFuture is called, it should throw a MISSING_PROPERTY error (CreateCertificateException).")
        void test1() {
            LocalDate date = null;
            assertTrue(LocalDateValidator.isDateNotInTheFuture(LocalDate.now()));
        }

        @Test
        @DisplayName("Given date is now, when validateDateIsNotInTheFuture is called, it should not throw an exception.")
        void test2() {
            assertTrue(LocalDateValidator.isDateNotInTheFuture(LocalDate.now()));
        }

        @Test
        @DisplayName("Given date is now minus 1 day, when validateDateIsNotInTheFuture is called, it should not throw an exception.")
        void test3() {
            assertTrue(LocalDateValidator.isDateNotInTheFuture(LocalDate.now().minusDays(1)));
        }

        @Test
        @DisplayName("Given date is now plus 1 day, when validateDateIsNotInTheFuture is called, it should throw a DATE_CANT_BE_AFTER error (CreateCertificateException).")
        void test4() {
            assertFalse(LocalDateValidator.isDateNotInTheFuture(LocalDate.now().plusDays(1)));
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotBeforeLimitDate")
    @DisplayName("validateDateIsNotBeforeLimitDate")
    class ValidateDateIsNotBeforeLimitDateTests {

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidatorTest#provideTestParameters")
        @DisplayName("Given limit-date and date, when validateDateIsNotBeforeLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> MethodSource")
        void test1(LocalDate limitDate, LocalDate date) {
            assertTrue(LocalDateValidator.isDateNotBeforeTheLimitDate(date, limitDate));
        }

        @ParameterizedTest
        @CsvSource({"20.09.1985,null,date", "null,20.09.1985,limitDate", "null,null,date"})
        @DisplayName("Given limit-date and date, when validateDateIsNotBeforeLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> CsvSource")
        void test2(String limitDateStr, String dateStr) {
            LocalDate limitDate = "null".equals(limitDateStr) ? null : LocalDate.parse(limitDateStr, LOCAL_DATE_FORMAT);
            LocalDate date = "null".equals(dateStr) ? null : LocalDate.parse(dateStr, LOCAL_DATE_FORMAT);
            assertTrue(LocalDateValidator.isDateNotBeforeTheLimitDate(date, limitDate));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is equal of greater than limit date, when validateDateIsNotBeforeLimitDate is called, it should not throw an exception.")
        void test3(int positiveDayOffset) {
            LocalDate date = LIMIT_DATE.plusDays(positiveDayOffset);
            assertTrue(LocalDateValidator.isDateNotBeforeTheLimitDate(date, LIMIT_DATE));
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 30, 365})
        @DisplayName("Given limit-date is " + LIMIT_DATE_STR + " and date is smaller than limit date, when validateDateIsNotBeforeLimitDate is called, it should throw a DATE_CANT_BE_BEFORE error (CreateCertificateException).")
        void test4(int negativeDayOffset) {
            LocalDate date = LIMIT_DATE.minusDays(negativeDayOffset);
            assertFalse(LocalDateValidator.isDateNotBeforeTheLimitDate(date, LIMIT_DATE));
        }
    }

    @Nested
    @Tag("LocalDateValidator.validateDateIsNotAfterLimitDate")
    @DisplayName("validateDateIsNotAfterLimitDate")
    class ValidateDateIsNotAfterLimitDateTests {

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidatorTest#provideTestParameters")
        @DisplayName("Given limit-date and date, when validateDateIsNotAfterLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> MethodSource")
        void test1(LocalDate limitDate, LocalDate date) {
            assertTrue(LocalDateValidator.isDateNotBeforeTheLimitDate(date, limitDate));
        }

        @ParameterizedTest
        @CsvSource({"20.09.1985,null,date", "null,20.09.1985,limitDate", "null,null,date"})
        @DisplayName("Given limit-date and date, when validateDateIsNotAfterLimitDate is called, it should throw a MISSING_PROPERTY error (CreateCertificateException). -> CsvSource")
        void test2(String limitDateStr, String dateStr, String text) {
            LocalDate limitDate = "null".equals(limitDateStr) ? null : LocalDate.parse(limitDateStr, LOCAL_DATE_FORMAT);
            LocalDate date = "null".equals(dateStr) ? null : LocalDate.parse(dateStr, LOCAL_DATE_FORMAT);
            assertTrue(LocalDateValidator.isDateNotBeforeTheLimitDate(date, limitDate));
        }
    }
}