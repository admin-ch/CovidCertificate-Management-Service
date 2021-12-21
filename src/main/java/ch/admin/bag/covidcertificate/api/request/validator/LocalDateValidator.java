package ch.admin.bag.covidcertificate.api.request.validator;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.MISSING_PROPERTY;
import static ch.admin.bag.covidcertificate.api.Constants.DATE_CANT_BE_BEFORE;
import static ch.admin.bag.covidcertificate.api.Constants.DATE_CANT_BE_AFTER;
import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;

public class LocalDateValidator {

    public static void validateDateIsNotMissing(LocalDate date, String propertyName) {
        if (date == null) {
            throw new CreateCertificateException(MISSING_PROPERTY, propertyName);
        }
    }

    public static void validateDateIsNotInThePast(LocalDate date, String propertyName) {
        validateDateIsNotMissing(date, propertyName);
        LocalDate now = LocalDate.now();
        if (date.isBefore(now)) {
            throw new CreateCertificateException(DATE_CANT_BE_BEFORE, "now");
        }
    }

    public static void validateDateIsNotInTheFuture(LocalDate date, String propertyName) {
        validateDateIsNotMissing(date, propertyName);
        LocalDate now = LocalDate.now();
        if (date.isAfter(now)) {
            throw new CreateCertificateException(DATE_CANT_BE_AFTER, "now");
        }
    }

    public static void validateDateIsNotBeforeLimitDate(LocalDate date, String propertyName, LocalDate limitDate) {
        validateDateIsNotMissing(date, propertyName);
        validateDateIsNotMissing(limitDate, "limitDate");
        if (date.isBefore(limitDate)) {
            throw new CreateCertificateException(DATE_CANT_BE_BEFORE, limitDate.format(LOCAL_DATE_FORMAT));
        }
    }

    public static void validateDateIsNotAfterLimitDate(LocalDate date, String propertyName, LocalDate limitDate) {
        validateDateIsNotMissing(date, propertyName);
        validateDateIsNotMissing(limitDate, "limitDate");
        if (date.isAfter(limitDate)) {
            throw new CreateCertificateException(DATE_CANT_BE_AFTER, limitDate.format(LOCAL_DATE_FORMAT));
        }
    }
}
