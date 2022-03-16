package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.AuthorizationException;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.util.Objects;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.IntStream;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String SPRING_ERROR_URI = "/error";
    private final AuthorizationService authorizationService;
    @Value("${cc-management-service.auth.allow-unauthenticated}")
    private String allowUnauthenticated;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (SPRING_ERROR_URI.equals(uri)) {
            return true;
        }

        String clientId = ((Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials())
                .getClaims()
                .get("client_id")
                .toString();

        if (Objects.areEqual(allowUnauthenticated, clientId)) {
            log.info("Allow unauthenticated because clientId is {}", clientId);
            return true;
        }

        ServiceData.Function function = authorizationService.getDefinition("management")
                .getFunctions()
                .values()
                .stream()
                .filter(f -> StringUtils.hasText(f.getUri()))
                .filter(f -> urisAreEqual(f.getUri(), uri))
                .findAny()
                .orElseThrow(() -> new AuthorizationException(Constants.NO_FUNCTION_CONFIGURED, uri));


        Set<String> roles = ((JeapAuthenticationToken) SecurityContextHolder
                .getContext()
                .getAuthentication())
                .getUserRoles();


        Set<String> permittedFunctions = authorizationService.getCurrent("management", new ArrayList<>(roles));

        if (!permittedFunctions.contains(function.getIdentifier())) {
            throw new AuthorizationException(Constants.FORBIDDEN, uri);
        }

        return true;
    }

    private boolean urisAreEqual(String uri1, String uri2) {
        String[] paths = uri1.split("/");
        String[] pathsToCompare = uri2.split("/");

        if (paths.length != pathsToCompare.length) {
            return false;
        }

        return IntStream.range(0, paths.length)
                .mapToObj(i -> Pair.of(paths[i], pathsToCompare[i]))
                .filter(pair -> !pair.getLeft().startsWith("{") && !pair.getLeft().endsWith("}"))
                .anyMatch(pair -> !Objects.areEqual(pair.getLeft(), pair.getRight()));


    }
}