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

@WebMvcTest(value = {ValueSetsController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles("test")
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
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/.well-known/openid-configuration")).willReturn(aResponse()
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
