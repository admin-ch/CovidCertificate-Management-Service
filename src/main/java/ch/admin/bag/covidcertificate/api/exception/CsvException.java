package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class CsvException extends NestedRuntimeException {
    private final CsvError error;
    private final byte[] csv;

    public CsvException(CsvError error, byte[] csv) {
        super(error.getErrorMessage());
        this.error = error;
        this.csv = csv;
    }
}
