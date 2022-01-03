package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

@Getter
public class CreateCertificateException extends NestedRuntimeException {
    private final CreateCertificateError error;

    public CreateCertificateException(CreateCertificateError error, Object... objects) {
        super(String.format(error.getErrorMessage(), objects));
        this.error = new CreateCertificateError(error.getErrorCode(), String.format(error.getErrorMessage(), objects), error.getHttpStatus());
    }
}
