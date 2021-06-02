package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityHelper {

    private final ServletJeapAuthorization jeapAuthorization;

    public boolean authorizeUser(HttpServletRequest request) {
        // A request to the OAuth2 protected resource includes the access token in the 'Authorization' header.
        // This token is the base of the Spring Security Authentication associated with the authenticated request.
        log.debug("Access token: {}.", request.getHeader("Authorization"));

        // Access the Spring Security Authentication as JeapAuthenticationToken
        JeapAuthenticationToken jeapAuthenticationToken = jeapAuthorization.getJeapAuthenticationToken();
        log.debug(jeapAuthenticationToken.toString());

        String displayName = jeapAuthenticationToken.getToken().getClaimAsString("displayName");

        if (displayName == null) {
            displayName = jeapAuthenticationToken.getTokenName();
        }

        if (jeapAuthenticationToken.getUserRoles().contains("bag-cc-superuser") &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-strongauth")) {
            log.warn("Superuser not allowed to use the application without strongauth...");
            log.warn("userroles: {}", jeapAuthenticationToken.getUserRoles());
            throw new AccessDeniedException("Access denied for Superuser without strongauth");
        }

        if (jeapAuthenticationToken.getUserRoles().contains("bag-cc-hin-epr") &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-hincode") &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-personal")) {

            log.warn("HIN-User not allowed to use the application...");
            log.warn("userroles: {}", jeapAuthenticationToken.getUserRoles());
            throw new AccessDeniedException("Access denied for HIN with CH-Login");
        }

        log.info("Authenticated User is '{}'.", displayName);

        return true;
    }
}
