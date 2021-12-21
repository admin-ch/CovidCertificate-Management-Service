package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Nested
    @Tag("Constants.CreateCertificateError")
    @DisplayName("Test the errors classes members values")
    class CreateCertificateErrorTests {

//        private void assertInvalidAddressError(CreateCertificateError createCertificateError) {
//            CreateCertificateException exception = assertThrows(CreateCertificateException.class, covidCertificateAddressDto::validate);
//            assertEquals(INVALID_ADDRESS.getHttpStatus(), exception.getError().getHttpStatus());
//            assertEquals(INVALID_ADDRESS.getErrorCode(), exception.getError().getErrorCode());
//            assertEquals(INVALID_ADDRESS.getErrorMessage(), exception.getError().getErrorMessage());
//        }
//
//        @Test
//        @DisplayName("Test INVALID_ADDRESS")
//        void INVALID_ADDRESS_TEST() {
//            assertEquals(INVALID_ADDRESS.getHttpStatus(), exception.getError().getHttpStatus());
//            assertEquals(INVALID_ADDRESS.getErrorCode(), exception.getError().getErrorCode());
//            assertEquals(INVALID_ADDRESS.getErrorMessage(), exception.getError().getErrorMessage());
//        }
    }

}