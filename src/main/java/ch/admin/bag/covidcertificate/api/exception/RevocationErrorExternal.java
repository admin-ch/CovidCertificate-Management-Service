package ch.admin.bag.covidcertificate.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class RevocationErrorExternal implements Serializable {
    private int errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;
    private LocalDateTime revocationDateTime;
}
