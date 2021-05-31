package ch.admin.bag.covidcertificate.config.security.authentication;

import org.springframework.security.core.context.SecurityContextHolder;

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

}
