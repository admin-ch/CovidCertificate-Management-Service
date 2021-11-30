package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CovidCertificateVaccinationValidationServiceTest {

    @InjectMocks
    private CovidCertificateVaccinationValidationService service;

    private void assertInvalidCountryOfVaccination(CreateCertificateException createCertificateException) {
        assertEquals(HttpStatus.BAD_REQUEST, createCertificateException.getError().getHttpStatus());
        assertEquals(457, createCertificateException.getError().getErrorCode());
        assertEquals("Invalid country of vaccination", createCertificateException.getError().getErrorMessage());
        assertEquals(Constants.INVALID_COUNTRY_OF_VACCINATION, createCertificateException.getError());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"CH", "ch"})
    @DisplayName("Given 'isoCountryCode' is blank or 'CH', when validated, it should throw a CreateCertificateException exception.")
    void validateCountryIsNotSwitzerlandTest1(String isoCountryCode) {
        CreateCertificateException createCertificateException = Assertions.assertThrows(CreateCertificateException.class, () -> {
            service.validateCountryIsNotSwitzerland(isoCountryCode);
        });
        assertInvalidCountryOfVaccination(createCertificateException);
    }

    @ParameterizedTest
    @ValueSource(strings = {"DZ", "..", "ZZ"})
    @DisplayName("Given 'isoCountryCode' is not blank and not 'CH', when validated, it should not throw an exception.")
    void validateCountryIsNotSwitzerlandTest2(String isoCountryCode) {
        Assertions.assertDoesNotThrow( () ->  service.validateCountryIsNotSwitzerland(isoCountryCode) );
    }
}