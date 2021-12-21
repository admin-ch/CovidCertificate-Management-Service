package ch.admin.bag.covidcertificate.api.request.validator;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;

import javax.validation.constraints.NotBlank;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_CANT_BE_AFTER;
import static ch.admin.bag.covidcertificate.api.Constants.MISSING_PROPERTY;
import static ch.admin.bag.covidcertificate.api.Constants.TEXT_INVALID_LENGTH;

public class TextValidator {

    public static void validateTextIsNotNullAndNotEmpty(String text, String propertyName) {
        if (text == null || text.isEmpty()) {
            throw new CreateCertificateException(MISSING_PROPERTY, propertyName);
        }
    }

    public static void validateTextIsNotBlank(String text, String propertyName) {
        validateTextIsNotNullAndNotEmpty(text, propertyName);
        if (text.isBlank()) {
            throw new CreateCertificateException(MISSING_PROPERTY, propertyName);
        }
    }

    public static void validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(String text, String propertyName, int maxLength) {
        validateTextIsNotBlank(text, propertyName);
        if (text.length() > maxLength) {
            throw new CreateCertificateException(TEXT_INVALID_LENGTH, propertyName, maxLength);
        }
    }
}
