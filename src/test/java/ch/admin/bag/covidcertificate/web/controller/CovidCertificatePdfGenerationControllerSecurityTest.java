package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.config.LocalDateTimeConverter;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificatePdfGenerateController.class, OAuth2SecuredWebConfiguration.class,
        AuthorizationInterceptor.class, AuthorizationService.class, AuthorizationConfig.class, RoleConfig.class,
        LocalDateTimeConverter.class})
@ActiveProfiles({"test", "authorization"})
class CovidCertificatePdfGenerationControllerSecurityTest extends AbstractSecurityTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/";

    @MockBean
    private CovidCertificateGenerationService covidCertificateGenerationService;

    @MockBean
    private KpiDataService kpiDataService;

    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(VaccinationCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(TestCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                any(RecoveryCertificateCreateDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));

        lenient().when(covidCertificateGenerationService.generateFromExistingCovidCertificate(
                any(VaccinationCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateFromExistingCovidCertificate(
                any(TestCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
        lenient().when(covidCertificateGenerationService.generateFromExistingCovidCertificate(
                any(RecoveryCertificatePdfGenerateRequestDto.class))).thenReturn(
                fixture.create(CovidCertificateResponseEnvelope.class));
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
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2))
                   .generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
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
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2))
                   .generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
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
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2))
                   .generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0))
                   .generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0))
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
