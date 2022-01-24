package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.springframework.security.oauth2.jwt.Jwt;

import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;

public class UserExtIdHelper {

    private UserExtIdHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String extractUserExtId(Jwt token, String userExtId, SystemSource systemSource) {
        String relevantUserExtId;
        if (token != null && token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY) != null) {
            relevantUserExtId = token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY);
        } else {
            relevantUserExtId = userExtId;
        }

        if (relevantUserExtId == null) {
            throw new IllegalStateException("No relevantUserExtId found in request or token for " + systemSource.name());
        }

        return relevantUserExtId;
    }
}
