package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
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

import java.io.IOException;
import java.time.LocalDateTime;


import static ch.admin.bag.covidcertificate.FixtureCustomization.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {CovidCertificateGenerationController.class, OAuth2SecuredWebConfiguration.class},
            properties="jeap.security.oauth2.resourceserver.authorization-server.jwk-set-uri=http://localhost:8182/.well-known/jwks.json")  // Avoid port 8180, see below
@ActiveProfiles("local")
class CovidCertificateGenerationControllerSecurityTest {
    @MockBean
    private SecurityHelper securityHelper;
    @MockBean
    private CovidCertificateGenerationService covidCertificateGenerationService;
    @MockBean
    private ServletJeapAuthorization jeapAuthorization;
    @MockBean
    private KpiDataService kpiDataService;
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
    }

    @BeforeEach
    void setupMocks() throws IOException {
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
    }
    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    
    @Nested
    class CreateVaccinationCertificate {
        private static final String URL = BASE_URL+"vaccination";
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
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class CreateTestCertificate {
        private static final String URL = BASE_URL+"test";
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
            var createDto = fixture.create(TestCertificateCreateDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
        }
    }

    @Nested
    class CreateRecoveryCertificate {
        private static final String URL = BASE_URL+"recovery";
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
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            callCreateCertificateWithToken(URL, mapper.writeValueAsString(createDto), tokenExpiration, userRole, status);
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
        switch(status) {
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
