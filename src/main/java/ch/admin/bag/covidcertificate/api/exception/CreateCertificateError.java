package ch.admin.bag.covidcertificate.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class CreateCertificateError implements Serializable {
    private final int errorCode;
    private final String errorMessage;
    private final HttpStatus httpStatus;

    @Override
    public String toString() {
        return "{\"errorCode\":" + errorCode + "," +
                "\"errorMessage\":\"" + errorMessage + "\"," +
                "\"httpStatus\":\"" + httpStatus.name() + "\"}";
    }
}
