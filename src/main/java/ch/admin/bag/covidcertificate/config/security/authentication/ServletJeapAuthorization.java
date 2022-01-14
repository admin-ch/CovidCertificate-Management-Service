package ch.admin.bag.covidcertificate.config.security.authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;

/**
 * This class provides methods to support authorization needs based on the current security context for Spring WebMvc applications.
 */
public class ServletJeapAuthorization {

    /**
     * Fetch the JeapAuthenticationToken from the current security context.
     *
     * @return The JeapAuthenticationToken extracted from the current security context.
     */
    public JeapAuthenticationToken getJeapAuthenticationToken() {
        return (JeapAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Fetch the JeapAuthenticationToken from the current security context.
     *
     * @return The JeapAuthenticationToken extracted from the current security context.
     */
    public String getExtIdInAuthentication() {
        var authentication = (JeapAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var jwt = authentication.getToken();
        if (jwt != null) {
            return jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY);
        }
        return null;
    }

}
