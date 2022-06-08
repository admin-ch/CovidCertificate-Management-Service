package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class ConvertCertificateException extends NestedRuntimeException {

    private final ConvertCertificateError error;

    public ConvertCertificateException(ConvertCertificateError error) {
        super(String.format(error.getErrorMessage()));
        this.error = new ConvertCertificateError(error.getErrorCode(), String.format(error.getErrorMessage()),
                                                 error.getHttpStatus());
    }

    public ConvertCertificateException(ConvertCertificateError error, Object... objects) {
        super(String.format(error.getErrorMessage(), objects));
        this.error = new ConvertCertificateError(error.getErrorCode(), String.format(error.getErrorMessage(), objects),
                                                 error.getHttpStatus());
    }
}
