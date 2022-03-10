package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.authorization.config.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.authorization.config.RoleData;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationInterceptorTest extends Assert {

    private final Object handler = new Object();
    @Mock
    private AuthorizationConfig authorizationConfig;
    @Mock
    private RoleConfig roleConfig;
    @Mock
    private MockServletContext mockServletContext;
    @Mock
    private MockHttpServletResponse response;
    private AuthorizationInterceptor interceptor;

    @Before
    public void setup() {
        SecurityContextHolder.getContext()
                .setAuthentication(JeapAuthenticationTestTokenBuilder.create()
                        .withClaim("client_id", "cc-management-ui")
                        .withUserRoles("MANDATORY_ROLE", "ONE_OF2")
                        .build());

        ServiceData serviceData = new ServiceData();

        serviceData.setFunctions(List.of(
                createFunction("/mandatoryRoleMissing", "MISSING_ROLE", null),
                createFunction("/mandatoryRolePresent", "MANDATORY_ROLE", null),
                createFunction("/oneOfRoleMissing", "MANDATORY_ROLE", List.of("MISSING_ROLE")),
                createFunction("/oneOfRolePresent", "MANDATORY_ROLE", List.of("ONE_OF1", "ONE_OF2", "ONE_OF3"))
        ));
        when(authorizationConfig.getManagement()).thenReturn(serviceData);

        List<RoleData> roles = Stream.of("MANDATORY_ROLE", "ONE_OF1", "ONE_OF2", "ONE_OF3")
                .map(role -> RoleData.builder()
                        .intern(role)
                        .claim(role)
                        .eiam(role)
                        .build()
                ).collect(Collectors.toList());
        when(roleConfig.getMappings()).thenReturn(roles);

        AuthorizationService authorizationService = new AuthorizationService(authorizationConfig, roleConfig);
        authorizationService.init();
        interceptor = new AuthorizationInterceptor(authorizationService);
        ReflectionTestUtils.setField(interceptor, "allowUnauthenticated", "allow-unauthenticated-client-id");
    }

    @Test
    public void testAllowUnauthenticated() {
        SecurityContextHolder.getContext()
                .setAuthentication(JeapAuthenticationTestTokenBuilder.create()
                        .withClaim("client_id", "allow-unauthenticated-client-id")
                        .build());

        MockHttpServletRequest request = get("/uriToNowhere")
                .buildRequest(mockServletContext);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void testNoFunctionConfigured() {
        MockHttpServletRequest request = get("/uriToNowhere")
                .buildRequest(mockServletContext);

        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> interceptor.preHandle(request, response, handler));
        assertEquals(exception.getError().getErrorCode(), Constants.NO_FUNCTION_CONFIGURED.getErrorCode());
    }

    @Test
    public void testMandatoryRoleMissing() {
        MockHttpServletRequest request = get("/mandatoryRoleMissing").buildRequest(mockServletContext);
        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> interceptor.preHandle(request, response, handler));
        assertEquals(exception.getError().getErrorCode(), Constants.FORBIDDEN.getErrorCode());
    }

    @Test
    public void testMandatoryRolePresent() {
        MockHttpServletRequest request = get("/mandatoryRolePresent").buildRequest(mockServletContext);
        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void testOneOfRoleMissing() {
        MockHttpServletRequest request = get("/oneOfRoleMissing").buildRequest(mockServletContext);
        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> interceptor.preHandle(request, response, handler));
        assertEquals(exception.getError().getErrorCode(), Constants.FORBIDDEN.getErrorCode());
    }


    @Test
    public void testOneOfRolePresent() {
        MockHttpServletRequest request = get("/oneOfRolePresent").buildRequest(mockServletContext);
        assertTrue(interceptor.preHandle(request, response, handler));
    }

    private ServiceData.Function createFunction(String uri, String mandatory, List<String> oneOf) {
        return ServiceData.Function.builder()
                .from(LocalDateTime.now())
                .until(LocalDateTime.now().plusDays(10))
                .identifier(uri)
                .mandatory(mandatory)
                .oneOf(oneOf)
                .uri(uri)
                .build();
    }


}
