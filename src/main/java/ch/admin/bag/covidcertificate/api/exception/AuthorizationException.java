package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

/**
 * Falls eine Funktion nicht konfiguriert ist, muss diese im
 * Projekt <code>cc-backend-authorization</code> konfiguriert werden.
 */
@Getter
public class AuthorizationException extends NestedRuntimeException {
    private final AuthorizationError error;

    public AuthorizationException(AuthorizationError error, Object... objects) {
        super(String.format(error.getErrorMessage(), objects));
        this.error = new AuthorizationError(error.getErrorCode(), String.format(error.getErrorMessage(), objects), error.getHttpStatus());
    }

}
