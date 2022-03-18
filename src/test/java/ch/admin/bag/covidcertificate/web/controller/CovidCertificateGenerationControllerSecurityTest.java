package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import ch.admin.bag.covidcertificate.testutil.KeyPairTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.*;
import static ch.admin.bag.covidcertificate.TestModelProvider.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificateGenerationController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles("test")
class CovidCertificateGenerationControllerSecurityTest {
    @MockBean
    private AuthorizationInterceptor interceptor;
    @MockBean
    private SecurityHelper securityHelper;
    @MockBean
    private CovidCertificateGenerationService covidCertificateGenerationService;
    @MockBean
    private ServletJeapAuthorization jeapAuthorization;
    @MockBean
    private KpiDataService kpiDataService;
    @MockBean
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    private static final JFixture fixture = new JFixture();

    private static final String BASE_URL = "/api/v1/covidcertificate/";
    private static final String VALID_USER_ROLE = "bag-cc-certificatecreator";
    private static final String VALID_SUPERUSER_ROLE = "bag-cc-superuser";
    private static final String INVALID_USER_ROLE = "invalid-role";
    // Avoid port 8180, which is likely used by the local KeyCloak:
    private static final int MOCK_SERVER_PORT = 8182;


    private static final KeyPairTestUtil KEY_PAIR_TEST_UTIL = new KeyPairTestUtil();
    private static final String PRIVATE_KEY = KEY_PAIR_TEST_UTIL.getPrivateKey();
    private static final LocalDateTime EXPIRED_IN_FUTURE = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime EXPIRED_IN_PAST = LocalDateTime.now().minusDays(1);
    private static final WireMockServer wireMockServer = new WireMockServer(options().port(MOCK_SERVER_PORT));


    @BeforeAll
    private static void setup() throws Exception {
        customizeVaccinationCertificateCreateDto(fixture);
        customizeTestCertificateCreateDto(fixture);
        customizeRecoveryCertificateCreateDto(fixture);

        wireMockServer.start();
        wireMockServer.stubFor(get(urlPathEqualTo("/.well-known/jwks.json")).willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(KEY_PAIR_TEST_UTIL.getJwks())));
        wireMockServer.stubFor(get(urlPathEqualTo("/.well-known/openid-configuration")).willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody("{\n" +
                        "  \"issuer\": \"http://localhost:8182\",\n" +
                        "  \"authorization_endpoint\": \"http://localhost:8182/oauth/authorize\",\n" +
                        "  \"token_endpoint\": \"http://localhost:8182/oauth/token\",\n" +
                        "  \"userinfo_endpoint\": \"http://localhost:8182/userinfo\",\n" +
                        "  \"end_session_endpoint\": \"http://localhost:8182/logout\",\n" +
                        "  \"jwks_uri\": \"http://localhost:8182/.well-known/jwks.json\",\n" +
                        "  \"grant_types_supported\": [\n" +
                        "    \"authorization_code\",\n" +
                        "    \"refresh_token\",\n" +
                        "    \"client_credentials\"\n" +
                        "  ],\n" +
                        "  \"response_types_supported\": [\n" +
                        "    \"code\",\n" +
                        "    \"none\",\n" +
                        "    \"id_token\",\n" +
                        "    \"token\",\n" +
                        "    \"id_token token\",\n" +
                        "    \"code id_token\",\n" +
                        "    \"code token\",\n" +
                        "    \"code id_token token\"\n" +
                        "  ],\n" +
                        "  \"subject_types_supported\": [\n" +
                        "    \"public\",\n" +
                        "    \"pairwise\"\n" +
                        "  ],\n" +
                        "  \"id_token_signing_alg_values_supported\": [\n" +
                        "    \"ES384\",\n" +
                        "    \"RS384\",\n" +
                        "    \"HS256\",\n" +
                        "    \"HS512\",\n" +
                        "    \"ES256\",\n" +
                        "    \"RS256\",\n" +
                        "    \"HS384\",\n" +
                        "    \"ES512\",\n" +
                        "    \"RS512\"\n" +
                        "  ],\n" +
                        "  \"userinfo_signing_alg_values_supported\": [\n" +
                        "    \"ES384\",\n" +
                        "    \"RS384\",\n" +
                        "    \"HS256\",\n" +
                        "    \"HS512\",\n" +
                        "    \"ES256\",\n" +
                        "    \"RS256\",\n" +
                        "    \"HS384\",\n" +
                        "    \"ES512\",\n" +
                        "    \"RS512\",\n" +
                        "    \"none\"\n" +
                        "  ],\n" +
                        "  \"request_object_signing_alg_values_supported\": [\n" +
                        "    \"ES384\",\n" +
                        "    \"RS384\",\n" +
                        "    \"ES256\",\n" +
                        "    \"RS256\",\n" +
                        "    \"ES512\",\n" +
                        "    \"RS512\",\n" +
                        "    \"none\"\n" +
                        "  ],\n" +
                        "  \"response_modes_supported\": [\n" +
                        "    \"query\",\n" +
                        "    \"fragment\",\n" +
                        "    \"form_post\"\n" +
                        "  ],\n" +
                        "  \"token_endpoint_auth_methods_supported\": [\n" +
                        "    \"private_key_jwt\",\n" +
                        "    \"client_secret_basic\",\n" +
                        "    \"client_secret_post\",\n" +
                        "    \"client_secret_jwt\"\n" +
                        "  ],\n" +
                        "  \"token_endpoint_auth_signing_alg_values_supported\": [\n" +
                        "    \"RS256\"\n" +
                        "  ],\n" +
                        "  \"claims_supported\": [\n" +
                        "    \"sub\",\n" +
                        "    \"iss\",\n" +
                        "    \"auth_time\",\n" +
                        "    \"name\",\n" +
                        "    \"given_name\",\n" +
                        "    \"family_name\",\n" +
                        "    \"preferred_username\",\n" +
                        "    \"ext_id\",\n" +
                        "    \"login_level\",\n" +
                        "    \"email\"\n" +
                        "  ],\n" +
                        "  \"claim_types_supported\": [\n" +
                        "    \"normal\"\n" +
                        "  ],\n" +
                        "  \"claims_parameter_supported\": false,\n" +
                        "  \"scopes_supported\": [\n" +
                        "    \"openid\",\n" +
                        "    \"offline_access\",\n" +
                        "    \"profile\",\n" +
                        "    \"roles\",\n" +
                        "    \"web-origins\"\n" +
                        "  ],\n" +
                        "  \"request_parameter_supported\": true,\n" +
                        "  \"request_uri_parameter_supported\": true,\n" +
                        "  \"code_challenge_methods_supported\": [\n" +
                        "    \"plain\",\n" +
                        "    \"S256\"\n" +
                        "  ],\n" +
                        "  \"tls_client_certificate_bound_access_tokens\": true,\n" +
                        "  \"introspection_endpoint\": \"http://localhost:8182/protocol/openid-connect/token/introspect\"\n" +
                        "}")));
    }

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
        lenient().when(interceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class))).thenReturn(true);

    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }


    @Nested
    class CreateVaccinationCertificate {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2)).generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(VaccinationCertificateCreateDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class CreateTestCertificate {
        private static final String URL = BASE_URL + "test";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateTestCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(1)).generateCovidCertificate(any(TestCertificateCreateDto.class));
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
            Mockito.verify(covidCertificateGenerationService, times(0)).generateCovidCertificate(any(RecoveryCertificateCreateDto.class));
        }

        private void callCreateRecoveryCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class GenerateVaccinationPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/vaccination";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2)).generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class GenerateTestPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/test";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2)).generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class GenerateRecoveryPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/recovery";

        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, VALID_SUPERUSER_ROLE, HttpStatus.OK);
            Mockito.verify(covidCertificateGenerationService, times(2)).generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callCreateVaccinationCertificateWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(covidCertificateGenerationService, times(0)).generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class));
        }

        private void callCreateVaccinationCertificateWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(pdfGenerateRequestDto), tokenExpiration, userRole, status);
        }
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
}
