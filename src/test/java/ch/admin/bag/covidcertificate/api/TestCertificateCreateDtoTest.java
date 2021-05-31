package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import org.junit.Test;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.NO_TEST_DATA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class TestCertificateCreateDtoTest {

    private final CovidCertificatePersonDto personDto = mock(CovidCertificatePersonDto.class);
    private final TestCertificateDataDto dataDto = mock(TestCertificateDataDto.class);

    @Test
    public void testNoTestData() {
        String language = "de";
        TestCertificateCreateDto testee = new TestCertificateCreateDto(
                personDto,
                null,
                language
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_TEST_DATA, exception.getError());

        testee = new TestCertificateCreateDto(
                personDto,
                List.of(),
                language
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(NO_TEST_DATA, exception.getError());

        testee = new TestCertificateCreateDto(
                personDto,
                List.of(dataDto),
                language
        );
        assertDoesNotThrow(testee::validate);
    }
}
