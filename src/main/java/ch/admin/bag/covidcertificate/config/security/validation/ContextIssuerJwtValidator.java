package ch.admin.bag.covidcertificate.config.security.validation;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
/**
 * This class implements a JwtValidator that checks if the access token for a given context has been issued by
 * a given issuer.
 */
public class ContextIssuerJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final Map<JeapAuthenticationContext, OAuth2TokenValidator<Jwt>> contextValidatorMap;

    public ContextIssuerJwtValidator(Map<JeapAuthenticationContext, String> contextIssuerMap) {
        Map<JeapAuthenticationContext, OAuth2TokenValidator<Jwt>> hashMap = new HashMap<>();
        for(JeapAuthenticationContext context : contextIssuerMap.keySet()) {
            JwtIssuerValidator validator = new JwtIssuerValidator(contextIssuerMap.get(context));
            hashMap.put(context, validator);
        }
        this.contextValidatorMap = Collections.unmodifiableMap(hashMap);
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        try {
            JeapAuthenticationContext context = JeapAuthenticationContext.readFromJwt(jwt);
            OAuth2TokenValidator<Jwt> contextIssuerJwtValidator = contextValidatorMap.get(context);
            if (contextIssuerJwtValidator != null) {
                return contextIssuerJwtValidator.validate(jwt);
            } else {
                return createErrorResult("Unsupported context claim value '" + context + "'.");
            }
        } catch (IllegalArgumentException e) {
            //This is the case if the context is not valid
            return createErrorResult(e.getMessage());
        }
    }

    private OAuth2TokenValidatorResult createErrorResult(String errorMessage) {
        OAuth2Error error = new OAuth2Error("invalid_token", errorMessage, null);
        log.warn(error.getDescription());
        return OAuth2TokenValidatorResult.failure(error);
    }
}
