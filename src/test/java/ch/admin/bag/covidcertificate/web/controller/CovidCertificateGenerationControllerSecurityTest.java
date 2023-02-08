package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getExceptionalCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryRatCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateCreateDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificateGenerationController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles("test")
class CovidCertificateGenerationControllerSecurityTest extends AbstractSecurityTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/";

    @MockBean
    private CovidCertificateGenerationService covidCertificateGenerationService;

    @MockBean
    private KpiDataService kpiDataService;

    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    @MockBean
    private AuthorizationService authorizationService;

    @BeforeAll
    static void setup() {
        customizeVaccinationCertificateCreateDto(fixture);
        customizeTestCertificateCreateDto(fixture);
        customizeRecoveryCertificateCreateDto(fixture);
    }

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(VaccinationCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(VaccinationTouristCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(TestCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(RecoveryCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(RecoveryRatCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(AntibodyCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(ExceptionalCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        Mockito.reset(authorizationService);
    }

    private void callCreateCertificateWithToken(String url, String requestBody, LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
        String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
        mockMvc.perform(post(url)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token)
                        .content(requestBody))
                .andExpect(getResultMatcher(status));
    }

    private ResultMatcher getResultMatcher(HttpStatus status) {
        switch (status) {
            case OK:
                return status().isOk();
            case FORBIDDEN:
                return status().isForbidden();
            case UNAUTHORIZED:
                return status().isUnauthorized();
            default:
                throw new IllegalArgumentException("HttpStatus not found!");
        }
    }

    @Nested
    class CreateVaccinationCertificate {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(1)).generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }

    @Nested
    class CreateVaccinationTouristCertificate {
        private static final String URL = BASE_URL + "vaccination-tourist";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateVaccinationTouristCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.FORBIDDEN);
            // Feature is deactivated
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationTouristCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE,
                                                             HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationTouristCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        private void callCreateVaccinationTouristCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getVaccinationTouristCertificateCreateDto("EU/1/20/1507", "de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }

    @Nested
    class CreateTestCertificate {
        private static final String URL = BASE_URL + "test";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateTestCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(1))
                   .generateCovidCertificate(any(TestCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateTestCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(TestCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateTestCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(TestCertificateCreateDto.class));
        }

        private void callCreateTestCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class CreateRecoveryCertificate {
        private static final String URL = BASE_URL + "recovery";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateRecoveryCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(1)).generateCovidCertificate(any(RecoveryCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateRecoveryCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(RecoveryCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateRecoveryCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(RecoveryCertificateCreateDto.class));
        }

        private void callCreateRecoveryCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }

    @Nested
    class CreateRecoveryRatCertificate {
        private static final String URL = BASE_URL + "recovery-rat";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateRecoveryRatCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(1))
                   .generateCovidCertificate(any(RecoveryRatCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateRecoveryRatCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(RecoveryRatCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateRecoveryRatCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(RecoveryRatCertificateCreateDto.class));
        }

        private void callCreateRecoveryRatCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getRecoveryRatCertificateCreateDto("de", "DB1E7078");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }

    @Nested
    class CreateAntibodyCertificate {
        private static final String URL = BASE_URL + "antibody";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateAntibodyCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.FORBIDDEN);
            // Feature is deactivated
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateAntibodyCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateAntibodyCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        private void callCreateAntibodyCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getAntibodyCertificateCreateDto("de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }

    @Nested
    class CreateExceptionalCertificate {
        private static final String URL = BASE_URL + "exceptional";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateExceptionalCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.FORBIDDEN);
            // Feature is deactivated
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateExceptionalCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateExceptionalCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateCovidCertificate(any(VaccinationTouristCertificateCreateDto.class));
        }

        private void callCreateExceptionalCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getExceptionalCertificateCreateDto("de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole,
                                           status);
        }
    }
}
