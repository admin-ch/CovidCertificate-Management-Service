package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.CsvException;
import ch.admin.bag.covidcertificate.api.exception.FeatureToggleException;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.exception.ValueSetException;
import ch.admin.bag.covidcertificate.api.exception.CacheNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
            var error = ex.getError();
            if (error != null) {
                return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("Create certificate exception, errorCode: {}", ex.getError().getErrorCode(), ex);
            return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
        }
    }

    @ExceptionHandler(value = {CsvException.class})
    protected ResponseEntity<Object> handleCsvException(CsvException ex) {
        log.warn("CSV create certificate request invalid", ex);
        return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(value = {RevocationException.class})
    protected ResponseEntity<Object> handleRevocationException(RevocationException ex) {
        return new ResponseEntity<>(ex.getError(), ex.getError().getHttpStatus());
    }

    @ExceptionHandler(value = {AccessDeniedException.class, SecurityException.class})
    protected ResponseEntity<Object> handleAccessDeniedException() {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    protected ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        Throwable cause = e.getCause().getCause();

        if(cause instanceof CreateCertificateException) {
            return this.handleCreateCertificateException((CreateCertificateException) cause);
        }
        return new ResponseEntity<>("Malformed Request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ValueSetException.class})
    protected ResponseEntity<Object> handleValueSetException(ValueSetException e) {
        return new ResponseEntity<>(e.getError(), e.getError().getHttpStatus());
    }

    @ExceptionHandler(value = {CacheNotFoundException.class})
    protected ResponseEntity<Object> handleCacheNotFoundException(CacheNotFoundException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {FeatureToggleException.class})
    protected ResponseEntity<Object> handleFeatureToggleException(FeatureToggleException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getError(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleException(Exception e) {
        log.error("Exception", e);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
