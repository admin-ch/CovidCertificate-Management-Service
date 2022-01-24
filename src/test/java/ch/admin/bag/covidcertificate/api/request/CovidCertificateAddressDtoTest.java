package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("CovidCertificateAddressDtoTest")
@DisplayName("Tests for the CovidCertificateAddressDto")
class CovidCertificateAddressDtoTest {

    private final int MAX_STREETANDNR_CHARS_LENGTH = 128;
    private final int MAX_CITY_CHARS_LENGTH = 128;
    private final int MIN_ZIPCODE_VALUE = 1000;
    private final int MAX_ZIPCODE_VALUE = 9999;

    private void assertInvalidAddressError(CovidCertificateAddressDto covidCertificateAddressDto) {
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, covidCertificateAddressDto::validate);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getHttpStatus());
        assertEquals(474, exception.getError().getErrorCode());
        assertEquals("Paper-based delivery requires a valid address.", exception.getError().getErrorMessage());
        assertEquals(INVALID_ADDRESS, exception.getError());
    }

    @Test
    @DisplayName("Given all the parameters for CovidCertificateAddressDto are valid, when validated, it should not throw an exception.")
    void CovidCertificateAddressDtoTest1() {
        var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", "BE");
        assertDoesNotThrow(covidCertificateAddressDto::validate);
    }

    @Nested
    @Tag("CovidCertificateAddressDto.cantonCodeSender")
    @DisplayName("'cantonCodeSender' must be a valid canton code or MI.")
    class CantonCodeSenderTests {

        @ParameterizedTest
        @ValueSource(strings = {"AG", "AI", "AR", "BE", "BL", "BS", "FR", "GE", "GL", "GR", "JU", "LU", "NE", "NW", "OW", "SG", "SH", "SO", "SZ", "TG", "TI", "UR", "VD", "VS", "ZG", "ZH", "MI"})
        @DisplayName("Given 'cantonCodeSender' is within the value set, when validated, it should not throw an exception.")
        void cantonCodeSenderTest1(String cantonCodeSender) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", cantonCodeSender);
            assertDoesNotThrow(covidCertificateAddressDto::validate);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "ZZ"})
        @DisplayName("Given 'cantonCodeSender' is blank or not in the value set, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cantonCodeSenderTest2(String cantonCodeSender) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, "city", cantonCodeSender);
            assertInvalidAddressError(covidCertificateAddressDto);
        }
    }

    @Nested
    @Tag("CovidCertificateAddressDto.streetAndNr")
    @DisplayName("'streetAndNr' can't be blank and can't exceed " + MAX_STREETANDNR_CHARS_LENGTH + " characters.")
    class StreetAndNumberTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'streetAndNr' is blank, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void streetAndNumberTest1(String streetAndNr) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto(streetAndNr, MIN_ZIPCODE_VALUE, "city", "BE");
            assertInvalidAddressError(covidCertificateAddressDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_STREETANDNR_CHARS_LENGTH - 1, MAX_STREETANDNR_CHARS_LENGTH})
        @DisplayName("Given 'streetAndNr' length <= " + MAX_STREETANDNR_CHARS_LENGTH + ", when validated, it should not throw an exception.")
        void streetAndNumberTest2(int streetAndNrLength) {
            String streetAndNumber = RandomStringUtils.random(streetAndNrLength, true, true);
            var covidCertificateAddressDto = new CovidCertificateAddressDto(streetAndNumber, MIN_ZIPCODE_VALUE, "city", "BE");
            assertDoesNotThrow(covidCertificateAddressDto::validate);
        }

        @Test
        @DisplayName("Given 'streetAndNr' length > " + MAX_STREETANDNR_CHARS_LENGTH + " characters, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void streetAndNumberTest3() {
            String streetAndNumber = RandomStringUtils.random(MAX_STREETANDNR_CHARS_LENGTH + 1, true, true);
            var covidCertificateAddressDto = new CovidCertificateAddressDto(streetAndNumber, MIN_ZIPCODE_VALUE, "city", "BE");
            assertInvalidAddressError(covidCertificateAddressDto);
        }
    }

    @Nested
    @Tag("CovidCertificateAddressDto.zipCode")
    @DisplayName("'zipCode' must be contained in the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval.")
    class ZipCodeTests {

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE - 1, MAX_ZIPCODE_VALUE + 1})
        @DisplayName("Given 'zipCode' is outside of the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void zipCodeTest1(int zipCode) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            assertInvalidAddressError(covidCertificateAddressDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE, MAX_ZIPCODE_VALUE})
        @DisplayName("Given 'zipCode' is equal to the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval limits, when validated, it should not throw an exception.")
        void zipCodeTest2(int zipCode) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            assertDoesNotThrow(covidCertificateAddressDto::validate);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_ZIPCODE_VALUE + 1, MAX_ZIPCODE_VALUE - 1})
        @DisplayName("Given 'zipCode' is contained in the [" + MIN_ZIPCODE_VALUE + ", " + MAX_ZIPCODE_VALUE + "] interval, when validated, it should not throw an exception.")
        void zipCodeTest3(int zipCode) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", zipCode, "city", "BE");
            assertDoesNotThrow(covidCertificateAddressDto::validate);
        }
    }

    @Nested
    @Tag("CovidCertificateAddressDtoTest.city")
    @DisplayName("'city' can't be blank and can't exceed " + MAX_CITY_CHARS_LENGTH + " characters length.")
    class CityTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("Given 'city' is blank, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cityTest1(String city) {
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            assertInvalidAddressError(covidCertificateAddressDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MAX_CITY_CHARS_LENGTH - 1, MAX_CITY_CHARS_LENGTH})
        @DisplayName("Given 'city' length <= " + MAX_CITY_CHARS_LENGTH + " characters, when validated, it should not throw an exception.")
        void cityTest2(int cityLength) {
            String city = RandomStringUtils.random(cityLength, true, true);
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            assertDoesNotThrow(covidCertificateAddressDto::validate);
        }

        @Test
        @DisplayName("Given 'city' length > " + MAX_STREETANDNR_CHARS_LENGTH + " characters, when validated, it should throw an INVALID_ADDRESS error (CreateCertificateException).")
        void cityTest3() {
            String city = RandomStringUtils.random(MAX_STREETANDNR_CHARS_LENGTH + 1, true, true);
            var covidCertificateAddressDto = new CovidCertificateAddressDto("streetAndNr", MIN_ZIPCODE_VALUE, city, "BE");
            assertInvalidAddressError(covidCertificateAddressDto);
        }
    }
}