package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import ch.admin.bag.covidcertificate.testutil.KeyPairTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ValueSetsController.class, OAuth2SecuredWebConfiguration.class},
        properties = "jeap.security.oauth2.resourceserver.authorization-server.jwk-set-uri=http://localhost:8182/.well-known/jwks.json")
// Avoid port 8180, see below
@ActiveProfiles("local")
class ValueSetsControllerSecurityTest {
    @MockBean
    private SecurityHelper securityHelper;
    @MockBean
    private ValueSetsService valueSetsService;
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    private static final JFixture fixture = new JFixture();

    private static final String URL = "/api/v1/valuesets";
    private static final String VALID_USER_ROLE = "bag-cc-certificatecreator";
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
        customizeVaccinationValueSet(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);

        wireMockServer.start();
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/.well-known/jwks.json")).willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(KEY_PAIR_TEST_UTIL.getJwks())));
    }

    @BeforeEach
    void setupMocks() {
        lenient().when(valueSetsService.getValueSets()).thenReturn(fixture.create(ValueSetsDto.class));
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @Nested
    class Get {
        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            callGetValueSetsWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(valueSetsService, times(1)).getValueSets();
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callGetValueSetsWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(valueSetsService, times(0)).getValueSets();
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callGetValueSetsWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(valueSetsService, times(0)).getValueSets();
        }
    }

    private void callGetValueSetsWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
        String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
        mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + token))
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