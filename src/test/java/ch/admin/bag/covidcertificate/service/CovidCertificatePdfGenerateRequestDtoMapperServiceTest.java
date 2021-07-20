package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.RecoveryCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.TestCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.VaccinationCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CovidCertificatePdfGenerateRequestDtoMapperServiceTest {
    @InjectMocks
    private CovidCertificatePdfGenerateRequestDtoMapperService service;
    @Mock
    private ValueSetsService valueSetsService;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void init(){
        customizeCountryCode(fixture);
        lenient().when(valueSetsService.getVaccinationValueSet(any())).thenReturn(fixture.create(VaccinationValueSet.class));
        lenient().when(valueSetsService.getAllTestValueSet(any(), any())).thenReturn(fixture.create(TestValueSet.class));
        lenient().when(valueSetsService.getCountryCode(any(), any())).thenReturn(fixture.create(CountryCode.class));
        lenient().when(valueSetsService.getCountryCodeEn(any())).thenReturn(fixture.create(CountryCode.class));
    }

    @Nested
    class ToVaccinationCertificatePdf{
        @Test
        void shouldLoadVaccinationValueSet(){
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.toVaccinationCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getVaccinationValueSet(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getMedicinalProduct());
        }

        @Test
        void shouldLoadCountryForCorrectCountryCode(){
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.toVaccinationCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCode(eq(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination()), any());
        }

        @Test
        void shouldLoadCountryInCorrectLanguage(){
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.toVaccinationCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCode(any(), eq(pdfGenerateRequestDto.getLanguage()));
        }

        @Test
        void shouldLoadCountryInEnglishForCorrectCountryCode(){
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            service.toVaccinationCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInTheSelectedLanguage(){
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toVaccinationCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInEnglish(){
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toVaccinationCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }

        @Test
        void shouldMapToVaccinationCertificatePdfWithCorrectPdfGenerateRequestDto(){
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            try (MockedStatic<VaccinationCertificatePdfGenerateRequestDtoMapper> vaccinationCertificatePdfGenerateRequestDtoMapperMock = Mockito.mockStatic(VaccinationCertificatePdfGenerateRequestDtoMapper.class)) {
                vaccinationCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(pdfGenerateRequestDto);

                vaccinationCertificatePdfGenerateRequestDtoMapperMock.verify(() ->
                        VaccinationCertificatePdfGenerateRequestDtoMapper
                                .toVaccinationCertificatePdf(eq(pdfGenerateRequestDto), any(), any(), any()));
            }
        }

            @Test
            void shouldMapToVaccinationCertificatePdfWithCorrectVaccinationValueSet(){
                var vaccinationValueSet = fixture.create(VaccinationValueSet.class);
                when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccinationValueSet);
                try (MockedStatic<VaccinationCertificatePdfGenerateRequestDtoMapper> vaccinationCertificatePdfGenerateRequestDtoMapperMock =
                             Mockito.mockStatic(VaccinationCertificatePdfGenerateRequestDtoMapper.class)) {
                    vaccinationCertificatePdfGenerateRequestDtoMapperMock
                            .when(() -> VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(any(), any(), any(), any()))
                            .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                    service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificatePdfGenerateRequestDto.class));

                    vaccinationCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                            VaccinationCertificatePdfGenerateRequestDtoMapper
                                    .toVaccinationCertificatePdf(any(), eq(vaccinationValueSet), any(), any()));
                }
        }

        @Test
        void shouldMapToVaccinationCertificatePdfWithCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<VaccinationCertificatePdfGenerateRequestDtoMapper> vaccinationCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfGenerateRequestDtoMapper.class)) {
                vaccinationCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificatePdfGenerateRequestDto.class));

                vaccinationCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        VaccinationCertificatePdfGenerateRequestDtoMapper
                                .toVaccinationCertificatePdf(any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdfWithCorrectCountryEnValueSet(){
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationCertificatePdfGenerateRequestDtoMapper> vaccinationCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfGenerateRequestDtoMapper.class)) {
                vaccinationCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificatePdfGenerateRequestDto.class));

                vaccinationCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        VaccinationCertificatePdfGenerateRequestDtoMapper
                                .toVaccinationCertificatePdf(any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnVaccinationCertificatePdf(){
            var vaccinationCertificatePdf = fixture.create(VaccinationCertificatePdf.class);
            try (MockedStatic<VaccinationCertificatePdfGenerateRequestDtoMapper> vaccinationCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfGenerateRequestDtoMapper.class)) {
                vaccinationCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(vaccinationCertificatePdf);
                var actual = service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificatePdfGenerateRequestDto.class));

                assertEquals(vaccinationCertificatePdf, actual);
            }
        }
    }

    @Nested
    class ToTestCertificatePdf{
        @Test
        void shouldLoadTestValueSetWithCorrectTestType(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.toTestCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getAllTestValueSet(eq(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTypeOfTest()), any());
        }

        @Test
        void shouldLoadTestValueSetWithCorrectManufacturer(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.toTestCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getAllTestValueSet(any(), eq(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTestManufacturer()));
        }

        @Test
        void shouldLoadCountryForCorrectCountryCode(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.toTestCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCode(eq(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest()), any());
        }

        @Test
        void shouldLoadCountryInCorrectLanguage(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.toTestCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCode(any(), eq(pdfGenerateRequestDto.getLanguage()));
        }

        @Test
        void shouldLoadCountryInEnglishForCorrectCountryCode(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            service.toTestCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInTheSelectedLanguage(){
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toTestCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInEnglish(){
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toTestCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }

        @Test
        void shouldMapToTestCertificatePdfWithCorrectPdfGenerateRequestDto(){
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            try (MockedStatic<TestCertificatePdfGenerateRequestDtoMapper> testCertificatePdfGenerateRequestDtoMapperMock = Mockito.mockStatic(TestCertificatePdfGenerateRequestDtoMapper.class)) {
                testCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(pdfGenerateRequestDto);

                testCertificatePdfGenerateRequestDtoMapperMock.verify(() ->
                        TestCertificatePdfGenerateRequestDtoMapper
                                .toTestCertificatePdf(eq(pdfGenerateRequestDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdfWithCorrectVaccinationValueSet(){
            var testValueSet = fixture.create(TestValueSet.class);
            when(valueSetsService.getAllTestValueSet(any(), any())).thenReturn(testValueSet);
            try (MockedStatic<TestCertificatePdfGenerateRequestDtoMapper> testCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(TestCertificatePdfGenerateRequestDtoMapper.class)) {
                testCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificatePdfGenerateRequestDto.class));

                testCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        TestCertificatePdfGenerateRequestDtoMapper
                                .toTestCertificatePdf(any(), eq(testValueSet), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdfWithCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<TestCertificatePdfGenerateRequestDtoMapper> testCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(TestCertificatePdfGenerateRequestDtoMapper.class)) {
                testCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificatePdfGenerateRequestDto.class));

                testCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        TestCertificatePdfGenerateRequestDtoMapper
                                .toTestCertificatePdf(any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdfWithCorrectCountryEnValueSet(){
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<TestCertificatePdfGenerateRequestDtoMapper> testCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(TestCertificatePdfGenerateRequestDtoMapper.class)) {
                testCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificatePdfGenerateRequestDto.class));

                testCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        TestCertificatePdfGenerateRequestDtoMapper
                                .toTestCertificatePdf(any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnVaccinationCertificatePdf(){
            var testCertificatePdf = fixture.create(TestCertificatePdf.class);
            try (MockedStatic<TestCertificatePdfGenerateRequestDtoMapper> testCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(TestCertificatePdfGenerateRequestDtoMapper.class)) {
                testCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(testCertificatePdf);
                var actual = service.toTestCertificatePdf(fixture.create(TestCertificatePdfGenerateRequestDto.class));

                assertEquals(testCertificatePdf, actual);
            }
        }
    }

    @Nested
    class ToRecoveryCertificatePdf{
       @Test
        void shouldLoadCountryInCorrectLanguage(){
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            service.toRecoveryCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCode(any(), eq(pdfGenerateRequestDto.getLanguage()));
        }

        @Test
        void shouldLoadCountryInEnglishForCorrectCountryCode(){
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            service.toRecoveryCertificatePdf(pdfGenerateRequestDto);
            verify(valueSetsService).getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0).getCountryOfTest());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInTheSelectedLanguage(){
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toRecoveryCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
        }

        @Test
        void shouldThrowAnExceptionIfCountryValueSetIsNullInEnglish(){
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(fixture.create(CountryCode.class));
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var exception = assertThrows(CreateCertificateException.class, () ->
                    service.toRecoveryCertificatePdf(pdfGenerateRequestDto)
            );
            assertEquals(INVALID_COUNTRY_OF_TEST, exception.getError());
        }

        @Test
        void shouldMapToRecoveryCertificatePdfWithCorrectPdfGenerateRequestDto(){
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            try (MockedStatic<RecoveryCertificatePdfGenerateRequestDtoMapper> recoveryCertificatePdfGenerateRequestDtoMapperMock = Mockito.mockStatic(RecoveryCertificatePdfGenerateRequestDtoMapper.class)) {
                recoveryCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(pdfGenerateRequestDto);

                recoveryCertificatePdfGenerateRequestDtoMapperMock.verify(() ->
                        RecoveryCertificatePdfGenerateRequestDtoMapper
                                .toRecoveryCertificatePdf(eq(pdfGenerateRequestDto), any(), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdfWithCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<RecoveryCertificatePdfGenerateRequestDtoMapper> recoveryCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfGenerateRequestDtoMapper.class)) {
                recoveryCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificatePdfGenerateRequestDto.class));

                recoveryCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        RecoveryCertificatePdfGenerateRequestDtoMapper
                                .toRecoveryCertificatePdf(any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdfWithCorrectCountryEnValueSet(){
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryCertificatePdfGenerateRequestDtoMapper> recoveryCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfGenerateRequestDtoMapper.class)) {
                recoveryCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificatePdfGenerateRequestDto.class));

                recoveryCertificatePdfGenerateRequestDtoMapperMock.verify(()->
                        RecoveryCertificatePdfGenerateRequestDtoMapper
                                .toRecoveryCertificatePdf(any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnRecoveryCertificatePdf(){
            var recoveryCertificatePdf = fixture.create(RecoveryCertificatePdf.class);
            try (MockedStatic<RecoveryCertificatePdfGenerateRequestDtoMapper> recoveryCertificatePdfGenerateRequestDtoMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfGenerateRequestDtoMapper.class)) {
                recoveryCertificatePdfGenerateRequestDtoMapperMock
                        .when(() -> RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(any(), any(), any()))
                        .thenReturn(recoveryCertificatePdf);
                var actual = service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificatePdfGenerateRequestDto.class));

                assertEquals(recoveryCertificatePdf, actual);
            }
        }
    }
}