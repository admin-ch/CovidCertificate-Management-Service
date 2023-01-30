package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import org.junit.Test;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NO_RECOVERY_DATA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class RecoveryCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final RecoveryCertificateDataDto dataDto = mock(RecoveryCertificateDataDto.class);

    @Test
    public void testNoRecoveryData() {
        String language = "de";
        RecoveryCertificateCreateDto testee = new RecoveryCertificateCreateDto(
                personDto,
                null,
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_RECOVERY_DATA, exception.getError());

        testee = new RecoveryCertificateCreateDto(
                personDto,
                List.of(),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_RECOVERY_DATA, exception.getError());

        testee = new RecoveryCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language,
                TestModelProvider.getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
        assertDoesNotThrow(testee::validate);
    }
}
