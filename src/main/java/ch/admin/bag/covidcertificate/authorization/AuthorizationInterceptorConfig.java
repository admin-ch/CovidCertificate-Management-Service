package ch.admin.bag.covidcertificate.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthorizationInterceptorConfig implements WebMvcConfigurer {
    private static final List<String> whitelistedUris = List.of(
            "/error",
            "/actuator/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/revocation-list",
            "/api/v1/ping",
            "/api/v1/signing/ping",
            "/api/v1/signing/health",
            "/api/v1/signing/info");

    private final AuthorizationInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).excludePathPatterns(whitelistedUris);
    }
}
