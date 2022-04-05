package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.config.LocalDateTimeConverter;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import ch.admin.bag.covidcertificate.testutil.JwtTestUtil;
import ch.admin.bag.covidcertificate.testutil.KeyPairTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeIssuableVaccineDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeIssuableVaccineDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ValueSetsController.class, OAuth2SecuredWebConfiguration.class, AuthorizationInterceptor.class, AuthorizationService.class, AuthorizationConfig.class, RoleConfig.class, LocalDateTimeConverter.class})
@ActiveProfiles({"test", "authorization"})
class ValueSetsControllerSecurityTest extends AbstractSecurityTest {
    private static final String URL = "/api/v1/valuesets";

    @MockBean
    private ValueSetsService valueSetsService;

    @BeforeAll
    private static void setup() {
        customizeIssuableVaccineDto(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);
    }

    @BeforeEach
    void setupMocks() {
        lenient().when(valueSetsService.getValueSets()).thenReturn(fixture.create(ValueSetsDto.class));
    }

    private void callGetValueSetsWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
        String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
        mockMvc
                .perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token))
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
}
