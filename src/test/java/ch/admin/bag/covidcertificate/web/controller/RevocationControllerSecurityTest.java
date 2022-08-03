package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.config.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.config.LocalDateTimeConverter;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {RevocationController.class, OAuth2SecuredWebConfiguration.class, AuthorizationInterceptor.class,
        AuthorizationService.class, AuthorizationConfig.class, RoleConfig.class, LocalDateTimeConverter.class})
@ActiveProfiles({"test", "authorization"})
class RevocationControllerSecurityTest extends AbstractSecurityTest {
    private static final String URL = "/api/v1/revocation";
    @MockBean
    private RevocationService revocationService;
    @MockBean
    private KpiDataService kpiDataService;
    @MockBean
    private ServletJeapAuthorization jeapAuthorization;

    @BeforeAll
    private static void setup() {
        customizeRevocationDto(fixture, false);
    }

    @BeforeEach
    void setupMocks() {
        lenient().when(revocationService.getRevocations())
                .thenReturn(fixture.collections().createCollection(List.class, String.class));
        lenient().doNothing().when(revocationService).createRevocation(anyString(), anyBoolean());
        lenient().when(jeapAuthorization.getJeapAuthenticationToken())
                .thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    private void callGetValueSetsWithToken(LocalDateTime tokenExpiration, String userRole, HttpStatus status) throws Exception {
        var createDto = fixture.create(RevocationDto.class);
        String token = JwtTestUtil.getJwtTestToken(PRIVATE_KEY, tokenExpiration, userRole);
        mockMvc.perform(post(URL)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", "Bearer " + token)
                        .content(mapper.writeValueAsString(createDto)))
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
    class Create {
        @Test
        void returnsOKIfAuthorizationTokenValid() throws Exception {
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.getRevocationDateTime(anyString())).thenReturn(null);
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaimAsString(anyString())).thenReturn("test");
            JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
            when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);

            callGetValueSetsWithToken(EXPIRED_IN_FUTURE, VALID_USER_ROLE, HttpStatus.OK);
            Mockito.verify(revocationService, times(1)).createRevocation(anyString(), anyBoolean());
        }

        @Test
        void returnsForbiddenIfAuthorizationTokenWithInvalidUserRole() throws Exception {
            callGetValueSetsWithToken(EXPIRED_IN_FUTURE, INVALID_USER_ROLE, HttpStatus.FORBIDDEN);
            Mockito.verify(revocationService, times(0)).createRevocation(anyString(), anyBoolean());
        }

        @Test
        void returnsUnauthorizedIfAuthorizationTokenExpired() throws Exception {
            callGetValueSetsWithToken(EXPIRED_IN_PAST, VALID_USER_ROLE, HttpStatus.UNAUTHORIZED);
            Mockito.verify(revocationService, times(0)).createRevocation(anyString(), anyBoolean());
        }
    }
}
