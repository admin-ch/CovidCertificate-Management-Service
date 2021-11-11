package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class ValueSetException extends NestedRuntimeException {
    private final ValueSetError error;

    public ValueSetException(ValueSetError error) {
        super(error.getErrorMessage());
        this.error = error;
    }
}
