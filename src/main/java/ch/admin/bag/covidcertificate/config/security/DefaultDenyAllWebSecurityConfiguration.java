package ch.admin.bag.covidcertificate.config.security;

import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * If the security starter has been added as a dependency but the OAuth2 secured web configuration has not been
 * activated e.g. because the needed configuration properties are missing, then deny access to all web endpoints as a
 * secure default web security configuration.
 */
@Configuration
@Slf4j
@AutoConfigureAfter(OAuth2SecuredWebConfiguration.class)
public class DefaultDenyAllWebSecurityConfiguration {

    private static final String DENY_ALL_MESSAGE = "jeap-spring-boot-security-starter did not activate OAuth2 resource security " +
                                                    "for web endpoints. Activating a 'deny-all' configuration as secure fallback. " +
                                                    "Override the 'deny-all' configuration with your own web security configuration " +
                                                    "or define the configuration properties needed for the OAuth2 resource security.";

    @Configuration
    @Order(500)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(ServletJeapAuthorization.class)
    @EnableWebSecurity
    public static class WebMvcDenyAllWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            log.debug(DENY_ALL_MESSAGE);
            http.authorizeRequests().anyRequest().denyAll().
             and().
             exceptionHandling().authenticationEntryPoint((new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)));
        }
    }

}
