package ch.admin.bag.covidcertificate.config.security.validation;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

class RawJwtTokenParser {

    static JeapAuthenticationContext extractAuthenticationContext(String token) {
        try {
            JWT jwt = parse(token);
            String contextClaimValue = jwt.getJWTClaimsSet().getStringClaim(JeapAuthenticationContext.getContextJwtClaimName());
            return JeapAuthenticationContext.valueOf(contextClaimValue);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(String.format("No valid authentication context extractable from JWT: %s.", e.getMessage()), e);
        }
    }

    private static JWT parse(String token) {
        try {
            return JWTParser.parse(token);
        } catch (Exception ex) {
            throw new IllegalArgumentException(String.format("Unparseable JWT: %s", ex.getMessage()), ex);
        }
    }
}
