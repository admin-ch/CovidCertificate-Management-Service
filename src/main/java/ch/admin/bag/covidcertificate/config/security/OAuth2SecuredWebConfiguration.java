package ch.admin.bag.covidcertificate.config.security;

import ch.admin.bag.covidcertificate.config.security.validation.AudienceJwtValidator;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationContext;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationConverter;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.config.security.validation.ContextIssuerJwtValidator;
import ch.admin.bag.covidcertificate.config.security.validation.JeapJwtDecoderFactory;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty("jeap.security.oauth2.resourceserver.authorization-server.issuer")
@Slf4j
public class OAuth2SecuredWebConfiguration {

    @Configuration
    @EnableConfigurationProperties({ResourceServerConfigProperties.class})
    public static class OAuth2SecuredWebCommonConfigurationProperties {

        private String applicationName;
        private ResourceServerConfigProperties resourceServer;

        public OAuth2SecuredWebCommonConfigurationProperties(
                @Value("${spring.application.name}") String applicationName,
                ResourceServerConfigProperties resourceServer) {
            this.applicationName = applicationName;
            this.resourceServer = resourceServer;
        }

        public String getResourceIdWithFallbackToApplicationName() {
            return StringUtils.isNotBlank(resourceServer.getResourceId()) ? resourceServer.getResourceId() : applicationName;
        }

        public ResourceServerConfigProperties getResourceServer() {
            return resourceServer;
        }
    }

    @Configuration
    @Order(499)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @EnableWebSecurity
    @EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
    @RequiredArgsConstructor
    public static class OAuth2SecuredWebMvcConfiguration extends WebSecurityConfigurerAdapter {

        private final OAuth2SecuredWebCommonConfigurationProperties commonConfiguration;

        @Override
        public void configure(HttpSecurity http) throws Exception {

            //All requests must be authenticated
            http.authorizeRequests()
                    .anyRequest()
                    .fullyAuthenticated();

            //Enable CORS
            http.cors();

            //Enable CSRF with CookieCsrfTokenRepository as can be used from Angular
            http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

            //No session management is needed, we want stateless
            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

            //Treat endpoints as OAuth2 resources
            http.oauth2ResourceServer().
                    jwt().
                    decoder(createJwtDecoder()).
                    jwtAuthenticationConverter(new JeapAuthenticationConverter());
        }

        @Bean
        public ServletJeapAuthorization jeapAuthorization() {
            return new ServletJeapAuthorization();
        }

        private JwtDecoder createJwtDecoder() {
            final String authorizationJwkSetUri = commonConfiguration.getResourceServer().getAuthorizationServer().getJwkSetUri();
            return JeapJwtDecoderFactory.createJwtDecoder(authorizationJwkSetUri, createTokenValidator(commonConfiguration));
        }
    }

    static OAuth2TokenValidator<Jwt> createTokenValidator(OAuth2SecuredWebCommonConfigurationProperties commonConfiguration) {
        return new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(30)),
                new AudienceJwtValidator(commonConfiguration.getResourceIdWithFallbackToApplicationName()),
                                         createContextIssuerJwtValidator(commonConfiguration.getResourceServer())
        );
    }

    static ContextIssuerJwtValidator createContextIssuerJwtValidator(ResourceServerConfigProperties resourceServerConfigProperties) {
        Map<JeapAuthenticationContext, String> contextIssuers = new HashMap<>();
        final String authorizationServer = resourceServerConfigProperties.getAuthorizationServer().getIssuer();
        contextIssuers.put(JeapAuthenticationContext.USER, authorizationServer);
        return new ContextIssuerJwtValidator(contextIssuers);
    }

}
