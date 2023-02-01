package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeIssuableVaccineDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {ValueSetsController.class, OAuth2SecuredWebConfiguration.class})
@ActiveProfiles({"test"})
class ValueSetsControllerSecurityTest extends AbstractSecurityTest {
    private static final String URL = "/api/v1/valuesets";

    @MockBean
    private ValueSetsService valueSetsService;
    @MockBean
    private AuthorizationService authorizationService;

    @BeforeAll
    private static void setup() {
        customizeIssuableVaccineDto(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);
    }

    @BeforeEach
    void setupMocks() {
        lenient().when(valueSetsService.getValueSets()).thenReturn(fixture.create(ValueSetsDto.class));
        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
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
            ServiceData.Function valuesets = createFunction("valueSets", "WEB_UI_USER", List.of(HttpMethod.GET));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.GET.name()))).thenReturn(List.of(valuesets));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), valuesets)).thenReturn(true);

            callGetValueSetsWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(valueSetsService, times(1)).getValueSets();
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            ServiceData.Function valuesets = createFunction("valueSets", "WEB_UI_USER", List.of(HttpMethod.GET));
            when(authorizationService.identifyFunction(
                    eq(AuthorizationService.SERVICE_MANAGEMENT),
                    startsWith(URL), eq(HttpMethod.GET.name()))).thenReturn(List.of(valuesets));
            when(authorizationService.isGranted(Set.of(VALID_USER_ROLE), valuesets)).thenReturn(false);

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
