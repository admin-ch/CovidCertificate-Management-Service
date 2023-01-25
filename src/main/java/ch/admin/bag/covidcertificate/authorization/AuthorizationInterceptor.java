package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

import static net.logstash.logback.argument.StructuredArguments.kv;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private final AuthorizationService authorizationService;

    @Value("${cc-management-service.auth.allow-unauthenticated}")
    private String allowUnauthenticated;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        log.trace("Call of preHandle with URI: {}", uri);

        JeapAuthenticationToken authentication = ((JeapAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication());

        String clientId = authentication.getClientId();
        if (Objects.areEqual(allowUnauthenticated, clientId)) {
            log.info("Allow unauthenticated because clientId is {}", clientId);
            return true;
        }

        Set<String> rawRoles = authentication.getUserRoles();
        if (!authorizationService.isUserPermitted(rawRoles)) {
            throw new AuthorizationException(Constants.ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN);
        }

        List<ServiceData.Function> functions = authorizationService.identifyFunction(uri, request.getMethod());

        if (functions.isEmpty()) {
            throw new AuthorizationException(Constants.NO_FUNCTION_CONFIGURED, uri);
        }

        if (functions.size() > 1) {
            throw new AuthorizationException(Constants.TOO_MANY_FUNCTIONS_CONFIGURED, uri, request.getMethod());
        }

        ServiceData.Function function = functions.get(0);

        log.info("Verify function authorization: {}, {}, {}",
                kv("clientId", clientId),
                kv("roles", authorizationService.mapRawRoles(rawRoles)),
                kv("function", function.getIdentifier()));

        boolean isGranted = authorizationService.isGranted(rawRoles, function);

        if (!isGranted) {
            throw new AuthorizationException(Constants.FORBIDDEN, uri);
        }

        return true;
    }
}
