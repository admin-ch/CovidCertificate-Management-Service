package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificateConversionRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificateConversionController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles("test")
class CovidCertificateConversionControllerSecurityTest extends AbstractSecurityTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/conversion/";

    @MockBean
    private CovidCertificateConversionService covidCertificateConversionService;

    @MockBean
    private KpiDataService kpiDataService;

    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    @MockBean
    private AuthorizationService authorizationService;

    @BeforeAll
    static void setup() {
        customizeVaccinationCertificateConversionRequestDto(fixture);
    }

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateConversionService.convertFromExistingCovidCertificate(
                any(VaccinationCertificateConversionRequestDto.class))).thenReturn(
                fixture.create(ConvertedCertificateResponseEnvelope.class));
        Mockito.reset(authorizationService);
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

            when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
            ServiceData.Function function = createFunction("function", "WEB_UI_USER", List.of(HttpMethod.POST));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.POST.name()))).thenReturn(List.of(function));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), function)).thenReturn(true);

            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateConversionService,
                           times(1)).convertFromExistingCovidCertificate(
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
