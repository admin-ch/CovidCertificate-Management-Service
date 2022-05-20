package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CsvErrorWithResponse extends CsvError {
    private final byte[] csv;

    public CsvErrorWithResponse(CreateCertificateError createCertificateError, byte[] csv) {
        super(createCertificateError);
        this.csv = csv;
    }
}
