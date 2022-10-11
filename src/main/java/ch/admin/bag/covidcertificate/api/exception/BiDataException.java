package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class BiDataException extends NestedRuntimeException {

    private final BiDataError biDataError;

    public BiDataException(BiDataError error) {
        super(error.getErrorMessage());
        this.biDataError = error;
    }
}
