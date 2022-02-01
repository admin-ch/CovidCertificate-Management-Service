package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_ANTIBODY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_EXCEPTIONAL;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY_RAT;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION_TOURIST;
import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;
import static ch.admin.bag.covidcertificate.api.valueset.TestType.PCR;
import static ch.admin.bag.covidcertificate.api.valueset.TestType.RAPID_TEST;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
class KpiDataServiceTest {
    @InjectMocks
    private KpiDataService service;
    @Mock
    private KpiDataRepository logRepository;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    private static void setup(){
        FixtureCustomization.customizeTestCertificateCreateDto(fixture);
        FixtureCustomization.customizeVaccinationCertificateCreateDto(fixture);
        FixtureCustomization.customizeRecoveryCertificateCreateDto(fixture);
        FixtureCustomization.customizeVaccinationTouristCertificateCreateDto(fixture);
        FixtureCustomization.customizeAntibodyCertificatePdf(fixture);
        FixtureCustomization.customizeExceptionalCertificateCreateDto(fixture);
    }

    @BeforeEach
    private void setupMocks(){
        var token = mock(JeapAuthenticationToken.class);
        var jwt = mock(Jwt.class);
        lenient().when(token.getToken()).thenReturn(jwt);
        lenient().when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(fixture.create(String.class));

        lenient().when(logRepository.save(any())).thenReturn(fixture.create(KpiData.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

    }

    @Nested
    class SaveKpi{
        @Test
        void savesKpiData(){
            var kpiData = fixture.create(KpiData.class);
            service.saveKpiData(kpiData);
            verify(logRepository).save(kpiData);
        }
    }

    @Nested
    class LogTestCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithTestType(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_TEST)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var uvci = fixture.create(String.class);
            service.logTestCertificateGenerationKpi(createDto, uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithPcrTypeOfTestAsDetails_ifTypeCodeIsPcr(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            ReflectionTestUtils.setField(createDto.getTestInfo().get(0), "typeCode", PCR.typeCode);
            service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals("pcr")));
        }

        @Test
        void savesKpiDataWithRapidTypeOfTestAsDetails_ifTypeCodeIsRapidTest(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            ReflectionTestUtils.setField(createDto.getTestInfo().get(0), "typeCode", RAPID_TEST.typeCode);
            service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals("rapid")));
        }

        @Test
        void savesKpiDataWithCountryOfTest(){
            var createDto = fixture.create(TestCertificateCreateDto.class);
            var countryOfVaccination = createDto.getTestInfo().get(0).getMemberStateOfTest();
            service.logTestCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(countryOfVaccination)));
        }
    }

    @Nested
    class LogVaccinationCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logVaccinationCertificateGenerationKpi(createDto, fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithVaccinationType(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            service.logVaccinationCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_VACCINATION)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logVaccinationCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var uvci = fixture.create(String.class);
            service.logVaccinationCertificateGenerationKpi(createDto, uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithMedicinalProductAsDetails(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var medicinalProduct = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();
            service.logVaccinationCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals(medicinalProduct)));
        }

        @Test
        void savesKpiDataWithCountryOfVaccination(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var countryOfVaccination = createDto.getVaccinationInfo().get(0).getCountryOfVaccination();
            service.logVaccinationCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(countryOfVaccination)));
        }
    }

    @Nested
    class LogVaccinationTouristCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logVaccinationTouristCertificateGenerationKpi(createDto, fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithVaccinationTouristType(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            service.logVaccinationTouristCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_VACCINATION_TOURIST)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logVaccinationTouristCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var uvci = fixture.create(String.class);
            service.logVaccinationTouristCertificateGenerationKpi(createDto, uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithMedicinalProductAsDetails(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var medicinalProduct = createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode();
            service.logVaccinationTouristCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals(medicinalProduct)));
        }

        @Test
        void savesKpiDataWithCountryOfVaccination(){
            var createDto = fixture.create(VaccinationTouristCertificateCreateDto.class);
            var countryOfVaccination = createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination();
            service.logVaccinationTouristCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(countryOfVaccination)));
        }
    }

    @Nested
    class LogRecoveryCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logRecoveryCertificateGenerationKpi(createDto, fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithRecoveryType(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            service.logRecoveryCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_RECOVERY)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logRecoveryCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var uvci = fixture.create(String.class);
            service.logRecoveryCertificateGenerationKpi(createDto, uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithNullDetails(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            service.logRecoveryCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails()==null));
        }

        @Test
        void savesKpiDataWithCountryOfTest(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var countryOfVaccination = createDto.getRecoveryInfo().get(0).getCountryOfTest();
            service.logRecoveryCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(countryOfVaccination)));
        }
    }

    @Nested
    class LogRecoveryRatCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithRecoveryRatType(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_RECOVERY_RAT)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var uvci = fixture.create(String.class);
            service.logRecoveryRatCertificateGenerationKpi(createDto, uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithPcrTypeOfTestAsDetails_ifTypeCodeIsPcr(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            ReflectionTestUtils.setField(createDto.getTestInfo().get(0), "typeCode", PCR.typeCode);
            service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals("pcr")));
        }

        @Test
        void savesKpiDataWithRapidTypeOfTestAsDetails_ifTypeCodeIsRapidTest(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            ReflectionTestUtils.setField(createDto.getTestInfo().get(0), "typeCode", RAPID_TEST.typeCode);
            service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails().equals("rapid")));
        }

        @Test
        void savesKpiDataWithCountryOfTest(){
            var createDto = fixture.create(RecoveryRatCertificateCreateDto.class);
            var countryOfVaccination = createDto.getTestInfo().get(0).getMemberStateOfTest();
            service.logRecoveryRatCertificateGenerationKpi(createDto, fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(countryOfVaccination)));
        }
    }

    @Nested
    class LogAntibodyCertificateGenerationKpi{
        @Test
        void savesKpiDataWithCurrentTimestamp(){
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logAntibodyCertificateGenerationKpi(fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithAntibodyType(){
            service.logAntibodyCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getType().equals(KPI_TYPE_ANTIBODY)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue(){
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logAntibodyCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci(){
            var uvci = fixture.create(String.class);
            service.logAntibodyCertificateGenerationKpi(uvci);

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithNotNullDetails(){
            service.logAntibodyCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getDetails()!=null));
        }

        @Test
        void savesKpiDataWithSwitzerlandAsCountry(){
            service.logAntibodyCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData)-> kpiData.getCountry().equals(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND)));
        }
    }

    @Nested
    class LogExceptionalCertificateGenerationKpi {
        @Test
        void savesKpiDataWithCurrentTimestamp() {
            var now = LocalDateTime.now();
            try (MockedStatic<LocalDateTime> localDateTimeMock = Mockito.mockStatic(LocalDateTime.class)) {
                localDateTimeMock.when(LocalDateTime::now).thenReturn(now);
                service.logExceptionalCertificateGenerationKpi(fixture.create(String.class));

                verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getTimestamp() == now));
            }
        }

        @Test
        void savesKpiDataWithExceptionalType() {
            service.logExceptionalCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getType().equals(KPI_TYPE_EXCEPTIONAL)));
        }

        @Test
        void savesKpiDataWithUsernameClaimKeyFromTokenAsValue() {
            var token = mock(JeapAuthenticationToken.class);
            var jwt = mock(Jwt.class);
            var usernameClaimKey = fixture.create(String.class);
            when(token.getToken()).thenReturn(jwt);
            when(jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)).thenReturn(usernameClaimKey);
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            service.logExceptionalCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getValue().equals(usernameClaimKey)));
        }

        @Test
        void savesKpiDataWithCorrectUvci() {
            var uvci = fixture.create(String.class);
            service.logExceptionalCertificateGenerationKpi(uvci);

            verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getUvci().equals(uvci)));
        }

        @Test
        void savesKpiDataWithNotNullDetails() {
            service.logExceptionalCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getDetails() != null));
        }

        @Test
        void savesKpiDataWithSwitzerlandAsCountry() {
            service.logExceptionalCertificateGenerationKpi(fixture.create(String.class));

            verify(logRepository).save(argThat((KpiData kpiData) -> kpiData.getCountry().equals(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND)));
        }
    }
}