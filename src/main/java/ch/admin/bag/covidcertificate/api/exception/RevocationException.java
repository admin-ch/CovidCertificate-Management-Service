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

    public RevocationException(CsvError error, Object... objects) {
        super(String.format(error.getErrorMessage(), objects));
        this.error = new RevocationError(error.getErrorCode(), String.format(error.getErrorMessage(), objects), error.getHttpStatus());
    }
}
