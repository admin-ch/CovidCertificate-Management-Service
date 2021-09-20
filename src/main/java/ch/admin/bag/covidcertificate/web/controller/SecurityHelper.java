package ch.admin.bag.covidcertificate.web.controller;

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

    private static final String CLIENT_ID_MANAGEMENT_UI = "cc-management-ui";

    private final ServletJeapAuthorization jeapAuthorization;

    public boolean authorizeUser(HttpServletRequest request) {
        // A request to the OAuth2 protected resource includes the access token in the 'Authorization' header.
        // This token is the base of the Spring Security Authentication associated with the authenticated request.
        log.trace("Access token: {}.", request.getHeader("Authorization"));

        // Access the Spring Security Authentication as JeapAuthenticationToken
        var jeapAuthenticationToken = jeapAuthorization.getJeapAuthenticationToken();
        log.trace(jeapAuthenticationToken.toString());

        if (jeapAuthenticationToken.getUserRoles().contains("bag-cc-superuser") &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-strongauth")) {
            log.warn("Superuser not allowed to use the application without strongauth...");
            log.warn("userroles: {}", jeapAuthenticationToken.getUserRoles());
            throw new AccessDeniedException("Access denied for Superuser without strongauth");
        }

        if ((jeapAuthenticationToken.getUserRoles().contains("bag-cc-hin-epr") || jeapAuthenticationToken.getUserRoles().contains("bag-cc-hin")) &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-hincode") &&
                !jeapAuthenticationToken.getUserRoles().contains("bag-cc-personal")) {

            log.warn("HIN-User not allowed to use the application...");
            log.warn("userroles: {}", jeapAuthenticationToken.getUserRoles());
            throw new AccessDeniedException("Access denied for HIN with CH-Login");
        }

        var clientId = jeapAuthenticationToken.getToken().getClaimAsString("azp");

        if (clientId == null) {
            clientId = jeapAuthenticationToken.getToken().getClaimAsString("client_id");
        }

        if (CLIENT_ID_MANAGEMENT_UI.equals(clientId)){
            var displayName = jeapAuthenticationToken.getToken().getClaimAsString("displayName");

            if (displayName == null) {
                displayName = jeapAuthenticationToken.getTokenName();
            }
            log.info("Received call from clientId '{}' with user is '{}'.", clientId, displayName);
        } else {
            log.info("Received call from clientId '{}'", clientId);
        }

        return true;
    }
}
