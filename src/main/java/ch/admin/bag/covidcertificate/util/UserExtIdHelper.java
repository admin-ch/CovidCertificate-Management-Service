package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.springframework.security.oauth2.jwt.Jwt;

import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;
import static ch.admin.bag.covidcertificate.service.KpiDataService.SERVICE_ACCOUNT_CC_API_GATEWAY_SERVICE;

public class UserExtIdHelper {

    private UserExtIdHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String extractUserExtId(Jwt token, String userExtId, SystemSource systemSource) {
        String relevantUserExtId = userExtId;

        if (token != null) {
            final String claimString = token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY);
            if (claimString != null && !SERVICE_ACCOUNT_CC_API_GATEWAY_SERVICE.equalsIgnoreCase(claimString)) {
                relevantUserExtId = claimString;
            }
        }

        if (relevantUserExtId == null) {
            throw new IllegalStateException("No relevantUserExtId found in request or token for " + systemSource.name());
        }

        return relevantUserExtId;
    }
}
