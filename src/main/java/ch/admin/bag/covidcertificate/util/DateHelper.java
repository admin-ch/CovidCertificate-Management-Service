package ch.admin.bag.covidcertificate.util;

import java.time.LocalDate;

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
}
