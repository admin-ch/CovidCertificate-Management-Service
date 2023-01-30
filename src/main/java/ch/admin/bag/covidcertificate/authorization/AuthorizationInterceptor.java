package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Objects;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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
        boolean isHinUser = rawRoles.contains("bag-cc-hin-epr") || rawRoles.contains("bag-cc-hin");
        boolean isHinCodeOrPersonal = rawRoles.contains("bag-cc-hincode") || rawRoles.contains("bag-cc-personal");
        if (isHinUser && !isHinCodeOrPersonal) {
            log.warn("HIN-User not allowed to use the application...");
            log.warn("userroles: {}", rawRoles);
            throw new AuthorizationException(Constants.ACCESS_DENIED_FOR_HIN_WITH_CH_LOGIN);
        }

        List<ServiceData.Function> functions = authorizationService.getDefinition("management")
                .getFunctions()
                .values()
                .stream()
                .filter(f -> StringUtils.hasText(f.getUri()))
                .filter(f -> f.matchesUri(uri))
                .filter(f -> f.matchesHttpMethod(request.getMethod()))
                .filter(f -> f.isBetween(LocalDateTime.now()))
                .toList();

        if (functions.isEmpty()) {
            throw new AuthorizationException(Constants.NO_FUNCTION_CONFIGURED, uri);
        }

        if (functions.size() > 1) {
            throw new AuthorizationException(Constants.TOO_MANY_FUNCTIONS_CONFIGURED, uri, request.getMethod());
        }

        ServiceData.Function function = functions.get(0);
        Set<String> roles = authorizationService.mapRawRoles(rawRoles);

        log.info("Verify function authorization: {}, {}, {}",
                kv("clientId", clientId),
                kv("roles", roles),
                kv("function", function.getIdentifier()));

        boolean isGranted = authorizationService.isGranted(roles, function);

        if (!isGranted) {
            throw new AuthorizationException(Constants.FORBIDDEN, uri);
        }

        return true;
    }
}
