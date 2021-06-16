package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedCantons;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

class CovidCertificateAddressDtoTest {

    @Test
    public void createsAddressDto__ifAllValid() {
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, "city", "BE");
        assertDoesNotThrow(addressDto::validate);
    }

    @Test
    public void throwsCertificateCreateException__onEmptyStreet() {
        var addressDto = new CovidCertificateAddressDto(null, 1000, "city", "BE");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        addressDto = new CovidCertificateAddressDto("", 1000, "city", "BE");
        exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        addressDto = new CovidCertificateAddressDto("   ", 1000, "city", "BE");
        exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void throwsCertificateCreateException__ifStreetTooLong() {
        String street = RandomStringUtils.random(129, true, true);
        var addressDto = new CovidCertificateAddressDto(street, 1000, "city", "BE");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void createsAddressDto__ifStreetLengthOk() {
        String street = RandomStringUtils.random(128, true, true);
        var addressDto = new CovidCertificateAddressDto(street, 1000, "city", "BE");
        assertDoesNotThrow(addressDto::validate);
    }

    @Test
    public void throwsCertificateCreateException__onEmptyCity() {
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, null, "BE");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, "", "BE");
        exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, "   ", "BE");
        exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void throwsCertificateCreateException__ifCityTooLong() {
        String city = RandomStringUtils.random(129, true, true);
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, city, "BE");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void createsAddressDto__ifCityLengthOk() {
        String city = RandomStringUtils.random(128, true, true);
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, city, "BE");
        assertDoesNotThrow(addressDto::validate);
    }

    @Test
    public void throwsCertificateCreateException__onInvalidZipCode() {
        // test low
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 999, "city", "BE");
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());

        // test high
        addressDto = new CovidCertificateAddressDto("streetAndNr", 10000, "city", "BE");
        exception = assertThrows(CreateCertificateException.class, addressDto::validate);
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    public void createsAddressDto__onZipCodeValid() {
        // test low
        var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, "city", "BE");
        assertDoesNotThrow(addressDto::validate);

        // test high
        addressDto = new CovidCertificateAddressDto("streetAndNr", 9999, "city", "BE");
        assertDoesNotThrow(addressDto::validate);
    }

    @Test
    public void throwsCertificateCreateException__onInvalidCantonCode() {
        try (MockedStatic<AcceptedCantons> allowedCantonsMock = Mockito.mockStatic(AcceptedCantons.class)) {
            var addressDto = new CovidCertificateAddressDto("streetAndNr", 1000, "city", "TT");
            allowedCantonsMock.when(() -> AcceptedCantons.isAccepted(any())).thenReturn(false);
            CreateCertificateException exception = assertThrows(CreateCertificateException.class, addressDto::validate);
            assertEquals(INVALID_ADDRESS, exception.getError());
            allowedCantonsMock.verify(times(1), () -> AcceptedCantons.isAccepted(any()));
        }
    }
}