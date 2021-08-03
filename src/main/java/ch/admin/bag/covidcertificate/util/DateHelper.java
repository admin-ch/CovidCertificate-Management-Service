package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static ch.admin.bag.covidcertificate.api.Constants.DAYS_UNTIL_RECOVERY_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS;

public class DateHelper {
    private DateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static LocalDate calculateValidFrom(LocalDate dateOfFirstPositiveTestResult) {
        return dateOfFirstPositiveTestResult.plusDays(DAYS_UNTIL_RECOVERY_VALID);
    }

    public static LocalDate calculateValidUntil(LocalDate dateOfFirstPositiveTestResult) {
        return dateOfFirstPositiveTestResult.plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS);
    }

    public static LocalDate parse(String date, CreateCertificateError possibleError) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return LocalDate.parse(date, formatter);
        } catch (Exception ex) {
            throw new CreateCertificateException(possibleError);
        }
    }
}
