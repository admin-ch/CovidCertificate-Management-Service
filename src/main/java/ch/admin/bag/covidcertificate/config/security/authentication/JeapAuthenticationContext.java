package ch.admin.bag.covidcertificate.config.security.authentication;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The supported authentication contexts.
 */
public enum JeapAuthenticationContext {

    USER;

    private static final String CONTEXT_CLAIM_NAME = "ctx";

    public static JeapAuthenticationContext readFromJwt(Jwt jwt) {
        String context = jwt.getClaimAsString(CONTEXT_CLAIM_NAME);
        if(context == null) {
            throw new IllegalArgumentException("Context claim '" + CONTEXT_CLAIM_NAME + "' is missing from the JWT.");
        }
        return JeapAuthenticationContext.valueOf(context);
    }

    public static String getContextJwtClaimName() {
        return CONTEXT_CLAIM_NAME;
    }

}
