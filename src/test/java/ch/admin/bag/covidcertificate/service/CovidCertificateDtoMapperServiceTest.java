package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryRatCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryRatCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateQrCode;
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
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        lenient().when(valueSetsService.getVaccinationValueSet(any())).thenReturn(fixture.create(IssuableVaccineDto.class));
        lenient().when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenReturn(fixture.create(IssuableTestDto.class));
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
        void shouldMapToVaccinationCertificateQrCode_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            try (MockedStatic<VaccinationCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificateQrCode.class));
                service.toVaccinationCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(() ->
                        VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(eq(createDto), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificateQrCode_withCorrectVaccinationValueSet() {
            var vaccineDto = fixture.create(IssuableVaccineDto.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccineDto);
            try (MockedStatic<VaccinationCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificateQrCode.class));
                service.toVaccinationCertificateQrCode(fixture.create(VaccinationCertificateCreateDto.class));

                vaccinationCertificateQrMapperMock.verify(() ->
                        VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(any(), eq(vaccineDto)));
            }
        }

        @Test
        void shouldReturnVaccinationCertificateQrCode() {
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
        void shouldMapToVaccinationCertificatePdf_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(createDto, fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(() ->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(eq(createDto), any(), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectVaccinationValueSet() {
            var vaccineDto = fixture.create(IssuableVaccineDto.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccineDto);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(() ->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), eq(vaccineDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectQrCodeData() {
            var qrCodeData = fixture.create(VaccinationCertificateQrCode.class);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), qrCodeData);

                vaccinationCertificatePdfMapperMock.verify(() ->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectCountryValueSet() {
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(() ->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToVaccinationCertificatePdf_withCorrectCountryEnValueSet() {
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationCertificatePdfMapper> vaccinationCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationCertificatePdfMapper.class)) {
                vaccinationCertificatePdfMapperMock
                        .when(() -> VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationCertificatePdf.class));
                service.toVaccinationCertificatePdf(fixture.create(VaccinationCertificateCreateDto.class), fixture.create(VaccinationCertificateQrCode.class));

                vaccinationCertificatePdfMapperMock.verify(() ->
                        VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(any(), any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnVaccinationCertificatePdf() {
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
    class ToVaccinationTouristCertificateQrCode {
        @Test
        void shouldLoadVaccinationTouristValueSet() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            service.toVaccinationTouristCertificateQrCode(createDto);
            verify(valueSetsService).getVaccinationValueSet(createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getVaccinationValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationTouristCertificateQrCode(createDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void shouldMapToVaccinationTouristCertificateQrCode_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            try (MockedStatic<VaccinationTouristCertificateQrCodeMapper> vaccinationTouristCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificateQrCodeMapper.class)) {
                vaccinationTouristCertificateQrMapperMock
                        .when(() -> VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificateQrCode.class));
                service.toVaccinationTouristCertificateQrCode(createDto);

                vaccinationTouristCertificateQrMapperMock.verify(() ->
                        VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(eq(createDto), any()));
            }
        }

        @Test
        void shouldMapToVaccinationTouristCertificateQrCode_withCorrectVaccinationValueSet() {
            var vaccineDto = fixture.create(IssuableVaccineDto.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccineDto);
            try (MockedStatic<VaccinationTouristCertificateQrCodeMapper> vaccinationTouristCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificateQrCodeMapper.class)) {
                vaccinationTouristCertificateQrMapperMock
                        .when(() -> VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificateQrCode.class));
                service.toVaccinationTouristCertificateQrCode(fixture.create(VaccinationTouristCertificateCreateDto.class));

                vaccinationTouristCertificateQrMapperMock.verify(() ->
                        VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(any(), eq(vaccineDto)));
            }
        }

        @Test
        void shouldReturnVaccinationTouristCertificateQrCode() {
            var vaccinationTouristCertificateQrCode = fixture.create(VaccinationTouristCertificateQrCode.class);
            try (MockedStatic<VaccinationTouristCertificateQrCodeMapper> vaccinationTouristCertificateQrMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificateQrCodeMapper.class)) {
                vaccinationTouristCertificateQrMapperMock
                        .when(() -> VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(any(), any()))
                        .thenReturn(vaccinationTouristCertificateQrCode);
                var actual = service.toVaccinationTouristCertificateQrCode(fixture.create(VaccinationTouristCertificateCreateDto.class));

                assertEquals(vaccinationTouristCertificateQrCode, actual);
            }
        }
    }

    @Nested
    class ToVaccinationTouristCertificatePdf {
        @Test
        void shouldLoadVaccinationTouristValueSet() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            service.toVaccinationTouristCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getVaccinationValueSet(createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode());
        }

        @Test
        void shouldLoadCountryCodeValueSetForSelectedLanguage() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            service.toVaccinationTouristCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCode(createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination(), createDto.getLanguage());
        }

        @Test
        void shouldLoadCountryCodeEnValueSet() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            service.toVaccinationTouristCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCodeEn(createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.getVaccinationValueSet(any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationTouristCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccinationTourist_ifCountryCodeValueSetIsNull() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationTouristCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }

        @Test
        void throwsInvalidCountryOfVaccinationTourist_ifCountryCodeEnValueSetIsNull() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toVaccinationTouristCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());
        }

        @Test
        void shouldMapToVaccinationTouristCertificatePdf_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
                service.toVaccinationTouristCertificatePdf(createDto, fixture.create(VaccinationTouristCertificateQrCode.class));

                vaccinationTouristCertificatePdfMapperMock.verify(() ->
                        VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(eq(createDto), any(), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationTouristCertificatePdf_withCorrectVaccinationTouristValueSet() {
            var vaccineDto = fixture.create(IssuableVaccineDto.class);
            when(valueSetsService.getVaccinationValueSet(any())).thenReturn(vaccineDto);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
                service.toVaccinationTouristCertificatePdf(fixture.create(VaccinationTouristCertificateCreateDto.class), fixture.create(VaccinationTouristCertificateQrCode.class));

                vaccinationTouristCertificatePdfMapperMock.verify(() ->
                        VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), eq(vaccineDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationTouristCertificatePdf_withCorrectQrCodeData() {
            var qrCodeData = fixture.create(VaccinationTouristCertificateQrCode.class);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
                service.toVaccinationTouristCertificatePdf(fixture.create(VaccinationTouristCertificateCreateDto.class), qrCodeData);

                vaccinationTouristCertificatePdfMapperMock.verify(() ->
                        VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToVaccinationTouristCertificatePdf_withCorrectCountryValueSet() {
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
                service.toVaccinationTouristCertificatePdf(fixture.create(VaccinationTouristCertificateCreateDto.class), fixture.create(VaccinationTouristCertificateQrCode.class));

                vaccinationTouristCertificatePdfMapperMock.verify(() ->
                        VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToVaccinationTouristCertificatePdf_withCorrectCountryEnValueSet() {
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(VaccinationTouristCertificatePdf.class));
                service.toVaccinationTouristCertificatePdf(fixture.create(VaccinationTouristCertificateCreateDto.class), fixture.create(VaccinationTouristCertificateQrCode.class));

                vaccinationTouristCertificatePdfMapperMock.verify(() ->
                        VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnVaccinationTouristCertificatePdf() {
            var vaccinationTouristCertificatePdf = fixture.create(VaccinationTouristCertificatePdf.class);
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<VaccinationTouristCertificatePdfMapper> vaccinationTouristCertificatePdfMapperMock =
                         Mockito.mockStatic(VaccinationTouristCertificatePdfMapper.class)) {
                vaccinationTouristCertificatePdfMapperMock
                        .when(() -> VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(vaccinationTouristCertificatePdf);
                var actual = service.toVaccinationTouristCertificatePdf(fixture.create(VaccinationTouristCertificateCreateDto.class), fixture.create(VaccinationTouristCertificateQrCode.class));

                assertEquals(vaccinationTouristCertificatePdf, actual);
            }
        }
    }

    @Nested
    class ToTestCertificateQrCode {
        @Test
        void shouldLoadTestValueSet() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            service.toTestCertificateQrCode(createDto);
            verify(valueSetsService).validateAndGetIssuableTestDto(createDto.getTestInfo().get(0).getTypeCode(), createDto.getTestInfo().get(0).getManufacturerCode());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toTestCertificateQrCode(createDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }


        @Test
        void shouldMapToTestCertificateQrCode_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            try (MockedStatic<TestCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(TestCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(TestCertificateQrCode.class));
                service.toTestCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(() ->
                        TestCertificateQrCodeMapper.toTestCertificateQrCode(eq(createDto), any()));
            }
        }

        @Test
        void shouldMapToTestCertificateQrCode_withCorrectTestValueSet() {
            var vaccinationValueSet = fixture.create(IssuableTestDto.class);
            when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenReturn(vaccinationValueSet);
            try (MockedStatic<TestCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(TestCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), any()))
                        .thenReturn(fixture.create(TestCertificateQrCode.class));
                service.toTestCertificateQrCode(fixture.create(TestCertificateCreateDto.class));

                vaccinationCertificateQrMapperMock.verify(() ->
                        TestCertificateQrCodeMapper.toTestCertificateQrCode(any(), eq(vaccinationValueSet)));
            }
        }

        @Test
        void shouldReturnTestCertificateQrCode() {
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
            verify(valueSetsService).validateAndGetIssuableTestDto(createDto.getTestInfo().get(0).getTypeCode(), createDto.getTestInfo().get(0).getManufacturerCode());
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
            lenient().when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenThrow(expected);

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
        void shouldMapToTestCertificatePdf_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(TestCertificateCreateDto.class);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(createDto, fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(() ->
                        TestCertificatePdfMapper.toTestCertificatePdf(eq(createDto), any(), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectTestValueSet() {
            var testValueSet = fixture.create(IssuableTestDto.class);
            when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenReturn(testValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(() ->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), eq(testValueSet), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectQrCodeData() {
            var qrCodeData = fixture.create(TestCertificateQrCode.class);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), qrCodeData);

                testCertificatePdfMapperMock.verify(() ->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryValueSet() {
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(() ->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryEnValueSet() {

            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<TestCertificatePdfMapper> testCertificatePdfMapperMock =
                         Mockito.mockStatic(TestCertificatePdfMapper.class)) {
                testCertificatePdfMapperMock
                        .when(() -> TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), any()))
                        .thenReturn(fixture.create(TestCertificatePdf.class));
                service.toTestCertificatePdf(fixture.create(TestCertificateCreateDto.class), fixture.create(TestCertificateQrCode.class));

                testCertificatePdfMapperMock.verify(() ->
                        TestCertificatePdfMapper.toTestCertificatePdf(any(), any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnTestCertificatePdf() {
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
    class ToRecoveryRatCertificateQrCode {
        @Test
        void shouldLoadTestValueSet() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            service.toRecoveryRatCertificateQrCode(createDto);
            verify(valueSetsService).validateAndGetIssuableTestDto(createDto.getTestInfo().get(0).getTypeCode(), createDto.getTestInfo().get(0).getManufacturerCode());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryRatCertificateQrCode(createDto)
            );

            assertEquals(expected.getError(), exception.getError());
        }


        @Test
        void shouldMapToTestCertificateQrCode_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            try (MockedStatic<RecoveryRatCertificateQrCodeMapper> qrMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificateQrCodeMapper.class)) {
                qrMapperMock
                        .when(() -> RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateQrCode(any()))
                        .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
                service.toRecoveryRatCertificateQrCode(createDto);

                qrMapperMock.verify(() ->
                        RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateQrCode(eq(createDto)));
            }
        }

        @Test
        void shouldReturnTestCertificateQrCode() {
            var vaccinationCertificateQrCode = fixture.create(RecoveryCertificateQrCode.class);
            try (MockedStatic<RecoveryRatCertificateQrCodeMapper> qrMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificateQrCodeMapper.class)) {
                qrMapperMock
                        .when(() -> RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateQrCode(any()))
                        .thenReturn(vaccinationCertificateQrCode);
                var actual = service.toRecoveryRatCertificateQrCode(fixture.create(RecoveryRatCertificateCreateDto.class));

                assertEquals(vaccinationCertificateQrCode, actual);
            }
        }
    }

    @Nested
    class ToRecoveryRatCertificatePdf {
        @Test
        void shouldLoadTestValueSet() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            service.toRecoveryRatCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).validateAndGetIssuableTestDto(createDto.getTestInfo().get(0).getTypeCode(), createDto.getTestInfo().get(0).getManufacturerCode());
        }

        @Test
        void shouldLoadCountryCodeValueSetForSelectedLanguage() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            service.toRecoveryRatCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCode(createDto.getTestInfo().get(0).getMemberStateOfTest(), createDto.getLanguage());
        }

        @Test
        void shouldLoadCountryCodeEnValueSet() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            service.toRecoveryRatCertificatePdf(createDto, qrCodeData);
            verify(valueSetsService).getCountryCodeEn(createDto.getTestInfo().get(0).getMemberStateOfTest());
        }

        @Test
        void throwsCreateCertificateException_ifValueSetServiceThrowsCreateCertificateException() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            var expected = fixture.create(CreateCertificateException.class);
            lenient().when(valueSetsService.validateAndGetIssuableTestDto(any(), any())).thenThrow(expected);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryRatCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(expected.getError(), exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifCountryCodeValueSetIsNull() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryRatCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }

        @Test
        void throwsInvalidCountryOfTest_ifCountryCodeEnValueSetIsNull() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(null);

            CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                    () -> service.toRecoveryRatCertificatePdf(createDto, qrCodeData)
            );

            assertEquals(INVALID_MEMBER_STATE_OF_TEST, exception.getError());
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            try (MockedStatic<RecoveryRatCertificatePdfMapper> recoveryRatCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificatePdfMapper.class)) {
                recoveryRatCertificatePdfMapperMock
                        .when(() -> RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryRatCertificatePdf(createDto, fixture.create(RecoveryCertificateQrCode.class));

                recoveryRatCertificatePdfMapperMock.verify(() ->
                        RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(eq(createDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectQrCodeData() {
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            try (MockedStatic<RecoveryRatCertificatePdfMapper> recoveryRatCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificatePdfMapper.class)) {
                recoveryRatCertificatePdfMapperMock
                        .when(() -> RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryRatCertificatePdf(fixture.create(RecoveryRatCertificateCreateDto.class), qrCodeData);

                recoveryRatCertificatePdfMapperMock.verify(() ->
                        RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryValueSet() {
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<RecoveryRatCertificatePdfMapper> recoveryRatCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificatePdfMapper.class)) {
                recoveryRatCertificatePdfMapperMock
                        .when(() -> RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryRatCertificatePdf(fixture.create(RecoveryRatCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryRatCertificatePdfMapperMock.verify(() ->
                        RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToTestCertificatePdf_withCorrectCountryEnValueSet() {
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryRatCertificatePdfMapper> recoveryRatCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificatePdfMapper.class)) {
                recoveryRatCertificatePdfMapperMock
                        .when(() -> RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryRatCertificatePdf(fixture.create(RecoveryRatCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryRatCertificatePdfMapperMock.verify(() ->
                        RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnTestCertificatePdf() {
            var recoveryCertificatePdf = fixture.create(RecoveryCertificatePdf.class);
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryRatCertificatePdfMapper> recoveryRatCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryRatCertificatePdfMapper.class)) {
                recoveryRatCertificatePdfMapperMock
                        .when(() -> RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(recoveryCertificatePdf);
                var actual = service.toRecoveryRatCertificatePdf(fixture.create(RecoveryRatCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                assertEquals(recoveryCertificatePdf, actual);
            }
        }
    }


    @Nested
    class ToRecoveryCertificateQrCode {
        @Test
        void shouldMapToRecoveryCertificateQrCode_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            try (MockedStatic<RecoveryCertificateQrCodeMapper> vaccinationCertificateQrMapperMock =
                         Mockito.mockStatic(RecoveryCertificateQrCodeMapper.class)) {
                vaccinationCertificateQrMapperMock
                        .when(() -> RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(any()))
                        .thenReturn(fixture.create(RecoveryCertificateQrCode.class));
                service.toRecoveryCertificateQrCode(createDto);

                vaccinationCertificateQrMapperMock.verify(() ->
                        RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(eq(createDto)));
            }
        }

        @Test
        void shouldReturnRecoveryCertificateQrCode() {
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
        void shouldMapToRecoveryCertificatePdf_withCorrectCertificateCreateDto() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(createDto, fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(() ->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(eq(createDto), any(), any(), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectQrCodeData() {
            var qrCodeData = fixture.create(RecoveryCertificateQrCode.class);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), qrCodeData);

                recoveryCertificatePdfMapperMock.verify(() ->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), eq(qrCodeData), any(), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectCountryValueSet() {
            var countryValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCode(any(), any())).thenReturn(countryValueSet);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(() ->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), eq(countryValueSet.getDisplay()), any()));
            }
        }

        @Test
        void shouldMapToRecoveryCertificatePdf_withCorrectCountryEnValueSet() {
            var countryEnValueSet = fixture.create(CountryCode.class);
            when(valueSetsService.getCountryCodeEn(any())).thenReturn(countryEnValueSet);
            try (MockedStatic<RecoveryCertificatePdfMapper> recoveryCertificatePdfMapperMock =
                         Mockito.mockStatic(RecoveryCertificatePdfMapper.class)) {
                recoveryCertificatePdfMapperMock
                        .when(() -> RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), any()))
                        .thenReturn(fixture.create(RecoveryCertificatePdf.class));
                service.toRecoveryCertificatePdf(fixture.create(RecoveryCertificateCreateDto.class), fixture.create(RecoveryCertificateQrCode.class));

                recoveryCertificatePdfMapperMock.verify(() ->
                        RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(any(), any(), any(), eq(countryEnValueSet.getDisplay())));
            }
        }

        @Test
        void shouldReturnRecoveryCertificatePdf() {
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