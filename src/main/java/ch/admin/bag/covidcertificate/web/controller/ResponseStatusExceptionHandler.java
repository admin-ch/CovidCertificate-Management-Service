package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ResponseStatusExceptionHandler {

    @ExceptionHandler(value = {CreateCertificateException.class})
    protected ResponseEntity<Object> handleCreateCertificateException(CreateCertificateException ex) {
        if (ex.getError().getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error(ex.getError().getErrorMessage(), ex);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            log.warn("Create certificate exception, errorCode: {}", ex.getError().getErrorCode(), ex);
            return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
        }
    }

    @ExceptionHandler(value = {RevocationException.class})
    protected ResponseEntity<Object> handleRevocationException(RevocationException ex) {
        return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class, SecurityException.class})
    protected ResponseEntity<Object> handleAccessDeniedException() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
