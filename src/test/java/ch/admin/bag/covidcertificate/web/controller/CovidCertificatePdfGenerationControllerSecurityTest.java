package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.CovidCertificateGeneratePdfFromExistingService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificateGeneratePdfFromExistingController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles({"test"})
class CovidCertificatePdfGenerationControllerSecurityTest extends AbstractSecurityTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/";

    @MockBean
    private CovidCertificateGeneratePdfFromExistingService covidCertificateGeneratePdfFromExistingService;

    @MockBean
    private KpiDataService kpiDataService;

    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    @MockBean
    private AuthorizationService authorizationService;

    @BeforeEach
    void setupMocks() {
        lenient().when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                any(VaccinationCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                any(TestCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                any(RecoveryCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
    }

    private void callCreateCertificateWithToken(
            String url, String requestBody, LocalDateTime tokenExpiration, String userRole, HttpStatus status)
            throws Exception {
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
    class GenerateVaccinationPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/vaccination";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            ServiceData.Function fromexisting = createFunction("fromexisting", "WEB_UI_USER", List.of(HttpMethod.GET));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(fromexisting));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), fromexisting)).thenReturn(true);

            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(1))
                   .generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration,
                                           userRole, status);
        }
    }

    @Nested
    class GenerateTestPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/test";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            ServiceData.Function fromexisting = createFunction("fromexisting", "WEB_UI_USER", List.of(HttpMethod.GET));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(fromexisting));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), fromexisting)).thenReturn(true);

            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(1))
                   .generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration,
                                           userRole, status);
        }
    }

    @Nested
    class GenerateRecoveryPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/recovery";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            ServiceData.Function fromexisting = createFunction("fromexisting", "WEB_UI_USER", List.of(HttpMethod.GET));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(fromexisting));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), fromexisting)).thenReturn(true);

            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(1))
                   .generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGeneratePdfFromExistingService, times(0))
                   .generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration,
                                           userRole, status);
        }
    }
}
