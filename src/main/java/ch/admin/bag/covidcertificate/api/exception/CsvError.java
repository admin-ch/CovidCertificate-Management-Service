package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CsvError extends CreateCertificateError {
    private final byte[] csv;

    public CsvError(CreateCertificateError createCertificateError, byte[] csv) {
        super(
                createCertificateError.getErrorCode(),
                createCertificateError.getErrorMessage(),
                createCertificateError.getHttpStatus()
        );
        this.csv = csv;
    }
}
