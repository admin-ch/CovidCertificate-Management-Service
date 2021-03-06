package ch.admin.bag.covidcertificate.api.request.validator;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;

public final class UvciValidator {

    private static final String REGEX_UVCI = "^urn:uvci:01:CH:[A-Z0-9]{24}$";

    private UvciValidator() {
    }

    public static boolean isValid(String uvci) {
        return uvci.matches(REGEX_UVCI);
    }

    public static void validateUvciMatchesSpecification(String uvci) {
        if (uvci == null || !isValid(uvci)) {
            throw new RevocationException(INVALID_UVCI);
        }
    }
}
