package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

import java.time.LocalDateTime;

@Getter
public class RevocationException extends NestedRuntimeException {
    private final RevocationError error;
    private final LocalDateTime revocationDateTime;

    public RevocationException(RevocationError error) {
        super(error.getErrorMessage());
        this.error = error;
        this.revocationDateTime = null;
    }

    public RevocationException(RevocationError error, LocalDateTime revocationDateTime) {
        super(error.getErrorMessage());
        this.error = error;
        this.revocationDateTime = revocationDateTime;
    }
}
