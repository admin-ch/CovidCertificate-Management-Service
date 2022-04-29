package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class RevocationException extends NestedRuntimeException {
    private final RevocationError error;

    public RevocationException(RevocationError error) {
        super(error.getErrorMessage());
        this.error = error;
    }
}
