package ch.admin.bag.covidcertificate.api.request.validator;

public class TextValidator {

    private TextValidator() {
    }

    public static boolean validateTextIsNotNullAndNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }

    public static boolean validateTextIsNotBlank(String text) {
        return validateTextIsNotNullAndNotEmpty(text) && !text.isBlank();
    }

    public static boolean validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(String text, int maxLength) {
        return validateTextIsNotBlank(text) && validateTextLengthIsNotBiggerThanMaxLength(text, maxLength);
    }

    public static boolean validateTextLengthIsNotBiggerThanMaxLength(String text, int maxLength) {
        return text == null || text.length() <= maxLength;
    }
}
