package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateHelperTest {

    private final JFixture fixture = new JFixture();

    @Nested
    class CalculateValidFrom{
        @Test
        void testCalculateValidFrom() {
            LocalDate date = LocalDate.of(2021, Month.FEBRUARY, 13);

            LocalDate validFrom = DateHelper.calculateValidFrom(date);

            assertEquals(date.plusDays(DAYS_UNTIL_RECOVERY_VALID), validFrom);
        }

    }

    @Nested
    class CalculateValidUntil{
        @Test
        void testCalculateValidUntil() {
            LocalDate date = LocalDate.of(2021, Month.FEBRUARY, 13);

            LocalDate validUntil = DateHelper.calculateValidUntilForRecoveryCertificate(date);

            assertEquals(date.plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS), validUntil);
        }
    }

    @Nested
    class ParseDateOfBirth{
        @Test
        void shouldParseYearOnly() {
            var date = fixture.create(LocalDate.class);
            var expected = LocalDate.of(date.getYear(), Month.JANUARY, 1);
            var dateString = date.format(DateTimeFormatter.ofPattern("yyyy"));

            var parsed = DateHelper.parseDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldParseYearAndMonth() {
            var date = fixture.create(LocalDate.class);
            var expected = LocalDate.of(date.getYear(), date.getMonth(), 1);
            var dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            var parsed = DateHelper.parseDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldParseIsoLocalDate() {
            var expected = fixture.create(LocalDate.class);
            var dateString = expected.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            var parsed = DateHelper.parseDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldParseFullDateSeparatedByDots() {
            var expected = fixture.create(LocalDate.class);
            var dateString = expected.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            var parsed = DateHelper.parseDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldThrowCreateCertificateExceptionIfUnknownDateFormat() {
            var expected = fixture.create(LocalDate.class);
            var dateString = expected.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            var exception = assertThrows(CreateCertificateException.class,
                    () -> DateHelper.parseDateOfBirth(dateString)
            );

            assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());
        }


        @Test
        void shouldThrowCreateCertificateExceptionIfNullDateString() {
            var exception = assertThrows(CreateCertificateException.class,
                    () -> DateHelper.parseDateOfBirth(null)
            );

            assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());
        }
    }

    @Nested
    class FormatDateOfBirth{
        @Test
        void shouldFormatYearOnly() {
            var date = fixture.create(LocalDate.class);
            var dateString = date.format(DateTimeFormatter.ofPattern("yyyy"));
            var expected = "--.--."+date.getYear();

            var parsed = DateHelper.formatDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldFormatYearAndMonth() {
            var date = fixture.create(LocalDate.class);
            var dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            var expected = date.format(DateTimeFormatter.ofPattern("--.MM.yyyy"));

            var parsed = DateHelper.formatDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldFormatIsoLocalDate() {
            var date = fixture.create(LocalDate.class);
            var dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            var expected = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            var parsed = DateHelper.formatDateOfBirth(dateString);

            assertEquals(expected, parsed);
        }

        @Test
        void shouldFormatFullDateSeparatedByDots() {
            var date = fixture.create(LocalDate.class);
            var dateString = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            var parsed = DateHelper.formatDateOfBirth(dateString);

            assertEquals(dateString, parsed);
        }

        @Test
        void shouldThrowCreateCertificateExceptionIfUnknownDateFormat() {
            var expected = fixture.create(LocalDate.class);
            var dateString = expected.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            var exception = assertThrows(CreateCertificateException.class,
                    () -> DateHelper.formatDateOfBirth(dateString)
            );

            assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());
        }


        @Test
        void shouldThrowCreateCertificateExceptionIfNullDateString() {
            var exception = assertThrows(CreateCertificateException.class,
                    () -> DateHelper.formatDateOfBirth(null)
            );

            assertEquals(INVALID_DATE_OF_BIRTH, exception.getError());
        }
    }
}
