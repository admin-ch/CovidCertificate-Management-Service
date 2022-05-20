package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
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
                String language,
                SystemSource systemSource
        ) {
            super(personData, language, null, null, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address,
                SystemSource systemSource
        ) {
            super(personData, language, address, null, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                String inAppDeliveryCode,
                SystemSource systemSource
        ) {
            super(personData, language, null, inAppDeliveryCode, systemSource);
        }

        public CertificateCreateDtoIml(
                CovidCertificatePersonDto personData,
                String language,
                CovidCertificateAddressDto address,
                String inAppDeliveryCode,
                SystemSource systemSource
        ) {
            super(personData, language, address, inAppDeliveryCode, systemSource);
        }
    }

    @Test
    public void throwsCertificateCreateException__onNoPersonData() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                null,
                language,
                addressDto,
                SystemSource.WebUI
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_PERSON_DATA, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto,
                SystemSource.WebUI
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void throwsCertificateCreateException__onInvalidLanguage() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                null,
                addressDto,
                SystemSource.WebUI
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                "en",
                addressDto,
                SystemSource.WebUI
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_LANGUAGE, exception.getError());

        testee = new CertificateCreateDtoIml(
                personDto,
                language,
                addressDto,
                SystemSource.WebUI
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void doesValidateSuccessfully() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto,
                "de",
                addressDto,
                SystemSource.WebUI
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void throwsException__ifAddressAndInAppDeliveryCodeArePassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto, "de", addressDto, RandomStringUtils.randomAlphanumeric(9), SystemSource.WebUI);

        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(DUPLICATE_DELIVERY_METHOD, exception.getError());
    }

    @Test
    public void throwsException__ifInvalidInAppDeliveryCode() {
        // test not alphanumeric
        var testee = new CertificateCreateDtoIml(
                personDto, "de", RandomStringUtils.random(9), SystemSource.WebUI);
        var exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_APP_CODE, exception.getError());
    }

    @Test
    public void validatesSuccessfully__ifAddressOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", addressDto, SystemSource.WebUI);
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void validatesSuccessfully__ifInAppDeliveryCodeOnlyIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(
                personDto, "de", RandomStringUtils.randomAlphanumeric(9), SystemSource.WebUI);
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void validatesSuccessfully__ifNoDeliveryIsPassed() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", SystemSource.WebUI);
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void validatesSuccessfully__ifInAppDeliveryCodeContainsSpaces() {
        CertificateCreateDto testee = new CertificateCreateDtoIml(personDto, "de", " 123 456 789 ", SystemSource.WebUI);
        assertDoesNotThrow(testee::validate);
        assertEquals("123456789", testee.getAppCode());
    }
}
