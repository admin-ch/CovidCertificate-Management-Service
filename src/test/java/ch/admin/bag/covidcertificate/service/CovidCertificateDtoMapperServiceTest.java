package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class CovidCertificateDtoMapperServiceTest {
    @InjectMocks
    private CovidCertificateDtoMapperService service;

    @Mock
    private ValueSetsService valueSetsService;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void setUp() {
        customizeTestValueSet(fixture);
        lenient().when(valueSetsService.getVaccinationValueSet(any())).thenReturn(fixture.create(VaccinationValueSet.class));
        lenient().when(valueSetsService.getTestValueSet(any())).thenReturn(fixture.create(TestValueSet.class));
        lenient().when(valueSetsService.getCountryCode(any(), any())).thenReturn(fixture.create(CountryCode.class));
        lenient().when(valueSetsService.getCountryCodeEn(any())).thenReturn(fixture.create(CountryCode.class));
    }

    @Nested
    class ToVaccinationCertificateQrCode {
        @Test
        void shouldLoadVaccinationValueSet() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            service.toVaccinationCertificateQrCode(createDto);
            verify(valueSetsService).getVaccinationValueSet(createDto.getVaccinationInfo().get(0).getMedicinalProductCode());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getVaccinationValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationCertificateQrCode(createDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }
    }

    @Nested
    class ToVaccinationCertificatePdf {
        @Test
        void shouldLoadVaccinationValueSet() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            service.toVaccinationCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getVaccinationValueSet(createDto.getVaccinationInfo().get(0).getMedicinalProductCode());
        }

        @Test
        void shouldLoadCountryCodeValueSetForSelectedLanguage() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            service.toVaccinationCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCode(createDto.getVaccinationInfo().get(0).getCountryOfVaccination(), createDto.getLanguage());
        }

        @Test
        void shouldLoadCountryCodeEnValueSet() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            service.toVaccinationCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCodeEn(createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getVaccinationValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccination_ifCountryCodeValueSetIsNull() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccination_ifCountryCodeEnValueSetIsNull() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }
    }


    @Nested
    class ToTestCertificateQrCode {
        @Test
        void shouldLoadTestValueSet() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            service.toTestCertificateQrCode(createDto);
            verify(valueSetsService).getTestValueSet(createDto.getTestInfo().get(0));
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getTestValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toTestCertificateQrCode(createDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }
    }

    @Nested
    class ToTestCertificatePdf {
        @Test
        void shouldLoadTestValueSet() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            service.toTestCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getTestValueSet(createDto.getTestInfo().get(0));
        }

        @Test
        void shouldLoadCountryCodeValueSetForSelectedLanguage() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            service.toTestCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCode(createDto.getTestInfo().get(0).getMemberStateOfTest(), createDto.getLanguage());
        }

        @Test
        void shouldLoadCountryCodeEnValueSet() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            service.toTestCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCodeEn(createDto.getTestInfo().get(0).getMemberStateOfTest());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getTestValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toTestCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifCountryCodeValueSetIsNull() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toTestCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifCountryCodeEnValueSetIsNull() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toTestCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }
    }



    @Nested
    class ToRecoveryCertificateQrCode {

    }

    @Nested
    class ToRecoveryCertificatePdf {
        @Test
        void shouldLoadCountryCodeValueSetForSelectedLanguage() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            service.toRecoveryCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCode(createDto.getRecoveryInfo().get(0).getCountryOfTest(), createDto.getLanguage());
        }

        @Test
        void shouldLoadCountryCodeEnValueSet() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            service.toRecoveryCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCodeEn(createDto.getRecoveryInfo().get(0).getCountryOfTest());
        }

        @Test
        void throwsInvalidCountryOfRecovery_ifCountryCodeValueSetIsNull() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
        }

        @Test
        void throwsInvalidCountryOfRecovery_ifCountryCodeEnValueSetIsNull() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
        }
    }
//
//    @Nested
//    class GenerateRecoveryCovidCertificate {
//        @Test
//        void shouldLoadCountryCodeValueSetForSelectedLanguage() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            service.generateCovidCertificate(createDto);
//            verify(valueSetsService).getCountryCode(createDto.getRecoveryInfo().get(0).getCountryOfTest(), createDto.getLanguage());
//        }
//
//        @Test
//        void shouldLoadCountryCodeEnValueSet() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            service.generateCovidCertificate(createDto);
//            verify(valueSetsService).getCountryCodeEn(createDto.getRecoveryInfo().get(0).getCountryOfTest());
//        }
//
//        @Test
//        void shouldCreateBarcode() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            var contents = fixture.create(String.class);
//            var objectWriter = mock(ObjectWriter.class);
//            when(objectMapper.writer()).thenReturn(objectWriter);
//            lenient().when(objectWriter.writeValueAsString(any())).thenReturn(contents);
//
//            service.generateCovidCertificate(createDto);
//
//            verify(barcodeService).createBarcode(contents);
//        }
//
//        @Test
//        void shouldCreatePdf() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            var qrCodeData = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
//            var countryCode = fixture.create(CountryCode.class);
//            var countryCodeEn = fixture.create(CountryCode.class);
//            var recoveryPdf = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
//            var barcode = fixture.create(Barcode.class);
//
//            when(valueSetsService.getCountryCode(createDto.getRecoveryInfo().get(0).getCountryOfTest(), createDto.getLanguage())).thenReturn(countryCode);
//            when(valueSetsService.getCountryCodeEn(createDto.getRecoveryInfo().get(0).getCountryOfTest())).thenReturn(countryCodeEn);
//            when(barcodeService.createBarcode(any())).thenReturn(barcode);
//
//            service.generateCovidCertificate(createDto);
//
//            verify(covidPdfCertificateGenerationService).generateCovidCertificate(recoveryPdf, barcode);
//        }
//
//        @Test
//        void shouldReturnBarcode() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            var barcode = fixture.create(Barcode.class);
//            when(barcodeService.createBarcode(any())).thenReturn(barcode);
//
//            var actual = service.generateCovidCertificate(createDto);
//
//            assertEquals(barcode.getImage(), actual.getQrCode());
//        }
//
//        @Test
//        void shouldReturnPdf() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            var pdf = fixture.create(byte[].class);
//            when(covidPdfCertificateGenerationService.generateCovidCertificate(any(), any())).thenReturn(pdf);
//
//            var actual = service.generateCovidCertificate(createDto);
//
//            assertEquals(pdf, actual.getPdf());
//        }
//
//        @Test
//        void shouldUVCI() throws IOException {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//
//            var actual = service.generateCovidCertificate(createDto);
//
//            assertNotNull(actual.getUvci());
//        }
//
//
//        @Test
//        void throwsInvalidCountryOfRecovery_ifCountryCodeValueSetIsNull() {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);
//
//            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));
//
//            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
//        }
//
//        @Test
//        void throwsInvalidCountryOfRecovery_ifCountryCodeEnValueSetIsNull() {
//            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
//            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);
//
//            CreateCertificateException exception = assertThrows(CreateCertificateException.class, () -> service.generateCovidCertificate(createDto));
//
//            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
//        }
//    }
}