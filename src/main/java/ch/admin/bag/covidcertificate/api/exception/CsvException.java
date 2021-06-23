package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class CsvException extends NestedRuntimeException {
    private final CsvError error;

    public CsvException(CsvError error) {
        super(error.getErrorMessage());
        this.error = error;
    }
}
