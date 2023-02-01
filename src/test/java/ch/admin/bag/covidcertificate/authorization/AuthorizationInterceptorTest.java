package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationError;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.authorization.config.LocalDateTimeConverter;
import ch.admin.bag.covidcertificate.authorization.config.RoleConfig;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AuthorizationInterceptor.class})
@ActiveProfiles(profiles = {"test"})
@EnableConfigurationProperties
public class AuthorizationInterceptorTest {
    private final Object handler = new Object();

    @Autowired
    private AuthorizationInterceptor interceptor;
    @Mock
    private MockServletContext mockServletContext;
    @Mock
    private MockHttpServletResponse response;
    @MockBean
    private AuthorizationService authorizationService;

    @Value("${cc-management-service.auth.allow-unauthenticated}")
    private String allowUnauthenticated;

    @BeforeEach
    void setupMocks() {
        Mockito.reset(authorizationService);
//        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
    }

    @Test
    public void testAllowUnauthenticated() {
        MockHttpServletRequest request = mockRequestWithClientId("/uriToNowhere", allowUnauthenticated);
        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void testNoFunctionConfigured() {
        MockHttpServletRequest request = mockRequest("/uriToNowhere");
        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
//        ServiceData.Function function = createFunction("function", "WEB_UI_USER",
//                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
//        when(authorizationService.identifyFunction(
//                eq(AuthorizationService.SERVICE_MANAGEMENT),
//                startsWith(request.getRequestURI()), eq(HttpMethod.GET.name()))).thenReturn(List.of(function));
//        when(authorizationService.isGranted(Set.of(""), valuesets)).thenReturn(true);

        assertError(request, Constants.NO_FUNCTION_CONFIGURED);
    }

    @Test
    public void testToManyFunctionConfigured() {
        MockHttpServletRequest request = mockRequest("/too-many-funcs");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function1 = createFunction("function1", "WEB_UI_USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        ServiceData.Function function2 = createFunction("function2", "WEB_UI_USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function1, function2));

        assertError(request, Constants.TOO_MANY_FUNCTIONS_CONFIGURED);
    }

    @Test
    public void testWrongHttpMethod() {
        MockHttpServletRequest request = mockRequest("/db", "WEB-USER");
        request.setMethod(HttpMethod.DELETE.name());

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function = createFunction("function", "WEB_UI_USER",
                request.getRequestURI(), HttpMethod.resolve("GET"));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function));
//        when(authorizationService.isGranted(anySet(),any())).thenReturn(false);

        assertError(request, Constants.FORBIDDEN);
    }

    @Test
    public void testCorrectHttpMethod() {
        MockHttpServletRequest request = mockRequest("/db", "ADMIN");
        request.setMethod(HttpMethod.DELETE.name());

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function = createFunction("function", "WEB_UI_USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function));
        when(authorizationService.isGranted(anySet(), eq(function))).thenReturn(true);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void testRoleMissing() {
        MockHttpServletRequest request = mockRequest("/only-web-user", "USELESS_ROLE");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function = createFunction("function", "WEB_UI_USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function));
        when(authorizationService.isGranted(Set.of(), function)).thenReturn(false);

        assertError(request, Constants.FORBIDDEN);
    }

    @Test
    public void testRolePresent() {
        MockHttpServletRequest request = mockRequest("/only-web-user", "WEB-USER");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function = createFunction("function", "WEB-USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function));
        when(authorizationService.isGranted(Set.of("WEB-USER"), function)).thenReturn(true);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void testAllowedInFuture() {
        MockHttpServletRequest request = mockRequest("/future", "WEB-USER");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of());

        assertError(request, Constants.NO_FUNCTION_CONFIGURED);
    }

    @Test
    public void testAllowedInPast() {
        MockHttpServletRequest request = mockRequest("/past", "WEB-USER");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of());

        assertError(request, Constants.NO_FUNCTION_CONFIGURED);
    }

    @Test
    public void userHinAuthorized() {
        MockHttpServletRequest request = mockRequest("/only-web-user", "WEB-USER", "bag-cc-hin", "bag-cc-hincode", "bac-cc-personal");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(true);
        ServiceData.Function function = createFunction("function", "WEB-USER",
                request.getRequestURI(), HttpMethod.resolve(request.getMethod()));
        when(authorizationService.identifyFunction(
                eq(AuthorizationService.SERVICE_MANAGEMENT),
                startsWith(request.getRequestURI()), eq(request.getMethod()))).thenReturn(List.of(function));
        when(authorizationService.isGranted(
                Set.of("WEB-USER", "bag-cc-hin", "bag-cc-hincode", "bac-cc-personal"),
                function)).thenReturn(true);

        assertTrue(interceptor.preHandle(request, response, handler));
    }

    @Test
    public void userHinNotAuthorized() {
        MockHttpServletRequest request = mockRequest("/only-web-user", "WEB-USER", "bag-cc-hin");

        when(authorizationService.isUserPermitted(Mockito.anyCollection())).thenReturn(false);

        assertError(request, Constants.ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN);
    }

    private MockHttpServletRequest mockRequest(String uri, String... roles) {
        return mockRequestWithClientId(uri, "cc-management-ui", roles);
    }

    private MockHttpServletRequest mockRequestWithClientId(String uri, String clientId, String... roles) {
        SecurityContextHolder.getContext()
                .setAuthentication(JeapAuthenticationTestTokenBuilder.create()
                        .withClaim("clientId", clientId)
                        .withUserRoles(roles)
                        .build());

        return get(uri).buildRequest(mockServletContext);
    }

    private void assertError(MockHttpServletRequest request, AuthorizationError error) {
        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> interceptor.preHandle(request, response, handler));
        assertEquals(error.getErrorCode(), exception.getError().getErrorCode());
    }

    protected ServiceData.Function createFunction(String identifier, String role, String uri, HttpMethod method) {
        ServiceData.Function function = new ServiceData.Function();
        function.setIdentifier(identifier);
        function.setFrom(LocalDateTime.MIN);
        function.setUntil(LocalDateTime.MAX);
        function.setOneOf(List.of(role));
        function.setUri(uri);
        function.setHttp(List.of(method));
        return function;
    }
}

