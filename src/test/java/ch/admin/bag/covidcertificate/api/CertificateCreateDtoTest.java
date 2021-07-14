package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final String language = "de";
    private final CovidCertificateAddressDto addressDto = new CovidCertificateAddressDto("street", 1000, "city", "BE");

    private static class CertificateCreateDtoIml extends CertificateCreateDto {
        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language
        ) {
            super(personData, language, null, null);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address
        ) {
            super(personData, language, address, null);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                String inAppDeliveryCode
        ) {
            super(personData, language, null, inAppDeliveryCode);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address,
                String inAppDeliveryCode
        ) {
            super(personData, language, address, inAppDeliveryCode);
        }
    }

    @Test
    public void throwsCertificateCreateException__onNoPersonData() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                null,
                language,
                addressDto
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void throwsCertificateCreateException__onInvalidLanguage() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                null,
                addressDto
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "en",
                addressDto
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void doesValidateSuccessfully() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                addressDto
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void throwsException__ifAddressAndInAppDeliveryCodeArePassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", addressDto, RandomStringUtils.randomAlphanumeric(9));

        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(DUPLICATE_DELIVERY_METHOD, exception.getError());
    }

    @Test
    public void throwsException__ifInvalidInAppDeliveryCode() {
        // test not alphanumeric
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", RandomStringUtils.random(9));
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_APP_CODE, exception.getError());
    }

    @Test
    public void validatesSuccessfully__ifAddressOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", addressDto);
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void validatesSuccessfully__ifInAppDeliveryCodeOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", RandomStringUtils.randomAlphanumeric(9));
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void validatesSuccessfully__ifNoDeliveryIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de");
        assertDoesNotThrow(testee::validate);
    }
}
