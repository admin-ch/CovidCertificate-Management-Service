package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.Test;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NO_VACCINATION_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class VaccinationCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final VaccinationCertificateDataDto dataDto = mock(VaccinationCertificateDataDto.class);

    @Test
    public void testNoVaccinationData() {
        String language = "de";
        VaccinationCertificateCreateDto testee = new VaccinationCertificateCreateDto(
                personDto,
                null,
                language,
                TestModelProvider.getCovidCertificateAddressDto()
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_VACCINATION_DATA, exception.getError());

        testee = new VaccinationCertificateCreateDto(
                personDto,
                List.of(),
                language,
                TestModelProvider.getCovidCertificateAddressDto()
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_VACCINATION_DATA, exception.getError());

        testee = new VaccinationCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language,
                TestModelProvider.getCovidCertificateAddressDto()
        );
        assertDoesNotThrow(testee::validate);
    }
}
