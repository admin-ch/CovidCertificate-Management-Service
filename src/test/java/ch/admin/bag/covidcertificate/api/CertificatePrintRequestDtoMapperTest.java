package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.mapper.CertificatePrintRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CertificatePrintRequestDtoMapperTest {

    @Test
    public void mapCertificateDtoToPrintRequestDto() {
        CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper = new CertificatePrintRequestDtoMapper();
        ReflectionTestUtils.setField(certificatePrintRequestDtoMapper, "sinceVaccinationDate", LocalDate.of(2021, Month.JUNE, 16));

        String language = "de";
        String uvci = "uvci:321";
        byte[] pdfStream = new byte[]{0, 1, 2, 3};
        VaccinationCertificateCreateDto createDto = TestModelProvider.getVaccinationCertificateCreateDto("medProdCode", language);
        CertificatePrintRequestDto testee = certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(pdfStream, uvci, createDto);

        assertEquals(testee.getLanguage(), language);
        assertEquals(testee.getAddressLine1(), createDto.getPersonData().getName().getGivenName() + " " + createDto.getPersonData().getName().getFamilyName());
        assertEquals(testee.getAddressLine2(), createDto.getAddress().getStreetAndNr());
        assertEquals(testee.getCantonCodeSender(), createDto.getAddress().getCantonCodeSender());
        assertEquals(testee.getCity(), createDto.getAddress().getCity());
        assertEquals(testee.getZipCode(), createDto.getAddress().getZipCode());
        assertEquals(testee.getUvci(), uvci);
        assertEquals(testee.getPdfCertificate(), pdfStream);
        assertFalse(testee.getIsBillable());
    }

    @Test
    public void mapBillableEqualsTrue() {
        CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper = new CertificatePrintRequestDtoMapper();
        ReflectionTestUtils.setField(certificatePrintRequestDtoMapper, "sinceVaccinationDate", LocalDate.of(2021, Month.APRIL, 29));

        VaccinationCertificateCreateDto createDto = TestModelProvider.getVaccinationCertificateCreateDto("", "");
        CertificatePrintRequestDto testee = certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(new byte[] {}, "", createDto);

        assertTrue(testee.getIsBillable());
    }

    @Test
    public void mapBillableEqualsFalse() {
        CertificatePrintRequestDtoMapper certificatePrintRequestDtoMapper = new CertificatePrintRequestDtoMapper();
        ReflectionTestUtils.setField(certificatePrintRequestDtoMapper, "sinceVaccinationDate", LocalDate.of(2021, Month.APRIL, 30));

        VaccinationCertificateCreateDto createDto = TestModelProvider.getVaccinationCertificateCreateDto("", "");
        CertificatePrintRequestDto testee = certificatePrintRequestDtoMapper.toCertificatePrintRequestDto(new byte[] {}, "", createDto);

        assertFalse(testee.getIsBillable());
    }

}
