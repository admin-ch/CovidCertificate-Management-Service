package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.*;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.*;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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

        @Test
        void shouldMapToVaccinationCertificateQrCode_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            try (MockedStatic<VaccinationCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificateQrCode.class));
                service.toVaccinationCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(()->
                        VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(eq(createDto), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificateQrCode_withCorrectVaccinationValueSet(){
            var vaccinationValueSet = fixture.create(VaccinationValueSet.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccinationValueSet);
            try (MockedStatic<VaccinationCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificateQrCode.class));
                service.toVaccinationCertificateQrCode(fixture.create(VaccinationCertificateCreateDto.class));

                vaccinationCertificateQrMapperMock.verify(()->
                        VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), eq(vaccinationValueSet)));
            }
        }

        @Test
        void shouldReturnVaccinationCertificateQrCode(){
            var vaccinationCertificateQrCode = fixture.create(VaccinationCertificateQrCode.class);
            try (MockedStatic<VaccinationCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), any()))
                        .thenReturn(vaccinationCertificateQrCode);
                var actual = service.toVaccinationCertificateQrCode(fixture.create(VaccinationCertificateCreateDto.class));

                assertEquals(vaccinationCertificateQrCode, actual);
            }
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

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(createDto, fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(()->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(eq(createDto), any(), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectVaccinationValueSet(){
            var vaccinationValueSet = fixture.create(VaccinationValueSet.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccinationValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(()->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), eq(vaccinationValueSet), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectQrCodeData(){
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), qrCodeData);

                vaccinationCertificatePdfMapperMock.verify(()->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(()->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectCountryEnValueSet(){
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(()->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnVaccinationCertificatePdf(){
            var vaccinationCertificatePdf = fixture.create(VaccinationCertificatePdf.class);
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(vaccinationCertificatePdf);
                var actual = service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                assertEquals(vaccinationCertificatePdf, actual);
            }
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


        @Test
        void shouldMapToTestCertificateQrCode_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            try (MockedStatic<TestCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(TestCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(TestCertificateQrCode.class));
                service.toTestCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(()->
                        TestCertificateQrCodeMapper.toTestCertificateQrCode(eq(createDto), any()));
            }
        }

        @Test
        void shouldMapToTestCertificateQrCode_withCorrectTestValueSet(){
            var vaccinationValueSet = fixture.create(TestValueSet.class);
            when(valueSetsService.getTestValueSet(any())).thenReturn(vaccinationValueSet);
            try (MockedStatic<TestCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(TestCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(TestCertificateQrCode.class));
                service.toTestCertificateQrCode(fixture.create(TestCertificateCreateDto.class));

                vaccinationCertificateQrMapperMock.verify(()->
                        TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), eq(vaccinationValueSet)));
            }
        }

        @Test
        void shouldReturnTestCertificateQrCode(){
            var vaccinationCertificateQrCode = fixture.create(TestCertificateQrCode.class);
            try (MockedStatic<TestCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(TestCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), any()))
                        .thenReturn(vaccinationCertificateQrCode);
                var actual = service.toTestCertificateQrCode(fixture.create(TestCertificateCreateDto.class));

                assertEquals(vaccinationCertificateQrCode, actual);
            }
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


        @Test
        void shouldMapToTestCertificatePdf_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(createDto, fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(()->
                        TestCertificatePdfMapper.toTestCertificatePdf(eq(createDto), any(), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectTestValueSet(){
            var testValueSet = fixture.create(TestValueSet.class);
            when(valueSetsService.getTestValueSet(any())).thenReturn(testValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(()->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), eq(testValueSet), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectQrCodeData(){
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), qrCodeData);

                testCertificatePdfMapperMock.verify(()->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(()->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryEnValueSet(){

            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(()->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnTestCertificatePdf(){
            var testCertificatePdf = fixture.create(TestCertificatePdf.class);
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(testCertificatePdf);
                var actual = service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                assertEquals(testCertificatePdf, actual);
            }
        }
    }



    @Nested
    class ToRecoveryCertificateQrCode {
        @Test
        void shouldMapToRecoveryCertificateQrCode_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            try (MockedStatic<RecoveryCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(RecoveryCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(any()))
                        .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
                service.toRecoveryCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(()->
                        RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(eq(createDto)));
            }
        }

        @Test
        void shouldReturnRecoveryCertificateQrCode(){
            var vaccinationCertificateQrCode = fixture.create(RecoveryCertificateQrCode.class);
            try (MockedStatic<RecoveryCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(RecoveryCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(any()))
                        .thenReturn(vaccinationCertificateQrCode);
                var actual = service.toRecoveryCertificateQrCode(fixture.create(RecoveryCertificateCreateDto.class));

                assertEquals(vaccinationCertificateQrCode, actual);
            }
        }
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

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectCertificateCreateDto(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(createDto, fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(()->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(eq(createDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectQrCodeData(){
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), qrCodeData);

                recoveryCertificatePdfMapperMock.verify(()->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectCountryValueSet(){
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(()->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectCountryEnValueSet(){
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(()->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnRecoveryCertificatePdf(){
            var recoveryCertificatePdf = fixture.create(RecoveryCertificatePdf.class);
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(recoveryCertificatePdf);
                var actual = service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                assertEquals(recoveryCertificatePdf, actual);
            }
        }
    }
}