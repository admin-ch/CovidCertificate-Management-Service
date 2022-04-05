package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.testutil.KeyPairTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public abstract class AbstractSecurityTest {
    static final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    static final JFixture fixture = new JFixture();
    static final KeyPairTestUtil KEY_PAIR_TEST_UTIL = new KeyPairTestUtil();
    static final String PRIVATE_KEY = KEY_PAIR_TEST_UTIL.getPrivateKey();
    static final LocalDateTime EXPIRED_IN_FUTURE = LocalDateTime.now().plusDays(1);
    static final LocalDateTime EXPIRED_IN_PAST = LocalDateTime.now().minusDays(1);
    static final String VALID_USER_ROLE = "bag-cc-certificatecreator";
    static final String VALID_SUPERUSER_ROLE = "bag-cc-superuser";
    static final String INVALID_USER_ROLE = "invalid-role";
    // Avoid port 8180, which is likely used by the local KeyCloak:
    private static final int MOCK_SERVER_PORT = 8182;
    private static final WireMockServer wireMockServer = new WireMockServer(options().port(MOCK_SERVER_PORT));
    @Autowired
    MockMvc mockMvc;

    @BeforeAll
    private static void setupAuthServer() throws Exception {
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

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }


}
