package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHelperTest {

    @InjectMocks
    private SecurityHelper securityHelper;

    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    @Test
    void authorizeUser_userAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        boolean authorizeUser = securityHelper.authorizeUser(request);
        assertTrue(authorizeUser);
    }

    @Test
    void authorizeUser_userHinErpAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).withUserRoles("bag-cc-hin-epr", "bag-cc-hincode", "bac-cc-personal").build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        boolean authorizeUser = securityHelper.authorizeUser(request);
        assertTrue(authorizeUser);
    }

    @Test
    void authorizeUser_userHinAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).withUserRoles("bag-cc-hin", "bag-cc-hincode", "bac-cc-personal").build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        boolean authorizeUser = securityHelper.authorizeUser(request);
        assertTrue(authorizeUser);
    }

    @Test
    void authorizeUser_userHinNotAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).withUserRoles("bag-cc-hin-epr").build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);

        assertThrows(AccessDeniedException.class, () -> securityHelper.authorizeUser(request));
    }

    @Test
    void authorizeUser_superuserAuthorized() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString(anyString())).thenReturn("test");
        JeapAuthenticationToken jeapAuthenticationToken = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt)
                                                                                            .withUserRoles(
                                                                                                    "bag-cc-superuser")
                                                                                            .build();
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        boolean authorizeUser = securityHelper.authorizeUser(request);
        assertTrue(authorizeUser);
    }
}
