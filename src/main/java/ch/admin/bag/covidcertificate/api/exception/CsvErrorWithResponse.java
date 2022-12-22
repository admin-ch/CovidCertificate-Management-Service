package ch.admin.bag.covidcertificate.api.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CsvErrorWithResponse extends CsvError {
    private final byte[] csv;

    public CsvErrorWithResponse(CreateCertificateError createCertificateError, byte[] csv) {
        super(createCertificateError);
        this.csv = csv;
    }
}
