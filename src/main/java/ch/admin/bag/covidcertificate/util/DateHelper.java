package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;

public class DateHelper {
    private static final Pattern YEAR_ONLY_DATE_PATTERN = Pattern.compile("^((19|20)\\d\\d)$");
    private static final Pattern YEAR_AND_MONTH_DATE_PATTERN = Pattern.compile("^((19|20)\\d\\d(-\\d\\d))$");
    private static final Pattern ISO_LOCAL_DATE_PATTERN = Pattern.compile("^((19|20)\\d\\d(-\\d\\d){2})$");
    private static final DateTimeFormatter PDF_CERTIFICATE_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private DateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate calculateValidFrom(LocalDate dateOfFirstPositiveTestResult) {
        return dateOfFirstPositiveTestResult.plusDays(DAYS_UNTIL_RECOVERY_VALID);
    }

    public static LocalDate calculateValidUntilForRecoveryCertificate(LocalDate dateOfFirstPositiveTestResult) {
        return dateOfFirstPositiveTestResult.plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS);
    }

    public static LocalDate calculateValidUntilForAntibodyCertificate(LocalDate dateOfTestResult) {
        return dateOfTestResult.plusDays(ANTIBODY_CERTIFICATE_VALIDITY_IN_DAYS);
    }

    public static LocalDate parse(String date, CreateCertificateError possibleError) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return LocalDate.parse(date, PDF_CERTIFICATE_DATE_FORMATTER);
        } catch (Exception ex) {
            throw new CreateCertificateException(possibleError);
        }
    }

    public static LocalDate parseDateOfBirth(String dateOfBirth) {
        DateTimeFormatter dateTimeFormatter;
        if (dateOfBirth == null) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
        if (YEAR_ONLY_DATE_PATTERN.matcher(dateOfBirth).matches()) {
            dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy")
                    .parseDefaulting(MONTH_OF_YEAR, 1)
                    .parseDefaulting(DAY_OF_MONTH, 1)
                    .toFormatter();
        } else if (YEAR_AND_MONTH_DATE_PATTERN.matcher(dateOfBirth).matches()) {
            dateTimeFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM")
                    .parseDefaulting(DAY_OF_MONTH, 1)
                    .toFormatter();
        } else if (ISO_LOCAL_DATE_PATTERN.matcher(dateOfBirth).matches()) {
            dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        } else {
            dateTimeFormatter = PDF_CERTIFICATE_DATE_FORMATTER;
        }
        try {
            return LocalDate.parse(dateOfBirth, dateTimeFormatter);
        } catch (DateTimeParseException ex) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
    }

    public static String formatDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }

        try {
            if (YEAR_ONLY_DATE_PATTERN.matcher(dateOfBirth).matches()) {
                return "--.--." + dateOfBirth;
            } else if (YEAR_AND_MONTH_DATE_PATTERN.matcher(dateOfBirth).matches()) {
                var dateParts = dateOfBirth.split("-");
                var year = dateParts[0];
                var month = dateParts[1];
                return String.format("--.%s.%s", month, year);
            } else if (ISO_LOCAL_DATE_PATTERN.matcher(dateOfBirth).matches()) {
                return LocalDate.parse(dateOfBirth).format(PDF_CERTIFICATE_DATE_FORMATTER);
            } else {
                return LocalDate.parse(dateOfBirth, PDF_CERTIFICATE_DATE_FORMATTER).format(PDF_CERTIFICATE_DATE_FORMATTER);
            }
        } catch (DateTimeParseException ex) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
    }
}
