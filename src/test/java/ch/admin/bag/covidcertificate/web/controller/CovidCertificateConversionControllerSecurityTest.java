package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.ProfileRegistry;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.CovidCertificateConversionService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificateConversionRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(value = {CovidCertificateConversionController.class, OAuth2SecuredWebConfiguration.class,
//        AuthorizationInterceptor.class, AuthorizationService.class, AuthorizationServiceImpl.class, AuthorizationConfig.class,
//        RoleConfig.class, LocalDateTimeConverter.class})
@WebMvcTest(value = {CovidCertificateConversionController.class, OAuth2SecuredWebConfiguration.class,
        AuthorizationInterceptor.class, AuthorizationService.class, AuthorizationConfig.class})
@ActiveProfiles({"test", ProfileRegistry.AUTHORIZATION_MOCK})
class CovidCertificateConversionControllerSecurityTest extends AbstractSecurityTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/conversion/";

    @MockBean
    private CovidCertificateConversionService covidCertificateConversionService;

    @MockBean
    private KpiDataService kpiDataService;

    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    @BeforeAll
    static void setup() {
        customizeVaccinationCertificateConversionRequestDto(fixture);
    }

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateConversionService.convertFromExistingCovidCertificate(
                any(VaccinationCertificateConversionRequestDto.class))).thenReturn(
                fixture.create(ConvertedCertificateResponseEnvelope.class));
    }

    private void callConvertCertificateWithToken(
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
    class ConvertVaccinationCertificate {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateConversionService,
                           times(2)).convertFromExistingCovidCertificate(
                    any(VaccinationCertificateConversionRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateConversionService,
                           times(0)).convertFromExistingCovidCertificate(
                    any(VaccinationCertificateConversionRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateConversionService,
                           times(0)).convertFromExistingCovidCertificate(
                    any(VaccinationCertificateConversionRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(
                LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var conversionDto = TestModelProvider.getVaccinationCertificateConversionRequestDto();
            callConvertCertificateWithToken(URL, mapper.writeValueAsString(conversionDto), tokenExpiration, userRole,
                                            status);
        }
    }
}
