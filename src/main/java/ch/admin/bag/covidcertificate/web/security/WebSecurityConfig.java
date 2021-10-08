package ch.admin.bag.covidcertificate.web.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * When including the jeap-spring-boot-security-starter dependency and providing the matching configuration properties
 * all web endpoints of the application will be automatically protected by OAuth2 as a default. If in addition web endpoints
 * with different protection (i.e. basic auth or no protection at all) must be provided at the same time by the application
 * an additional WebSecurityConfigurerAdapter configuration (like the one below) needs to explicitly punch a hole into
 * the jeap-spring-boot-security-starter OAuth2 protection with an appropriate HttpSecurity configuration.
 * Note: jeap-spring-boot-monitoring-starter already does exactly that for the prometheus actuator endpoint.
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${cc-management-service.allowed-origin}")
    private String allowedOrigin;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().
                antMatchers("/actuator/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
                            "/api/v1/revocation-list", "/api/v1/ping").
                    and().
                authorizeRequests().anyRequest().permitAll();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
