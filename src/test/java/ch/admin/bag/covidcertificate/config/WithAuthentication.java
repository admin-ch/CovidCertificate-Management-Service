package ch.admin.bag.covidcertificate.config;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Make a test method run with the WithAuthenticationExtension Junit 5 extension and specify the authentication factory method
 * to be used by the extension to populate the SpringSecurityContext the test method is executed with.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@ExtendWith(WithAuthenticationExtension.class)
public @interface WithAuthentication {
    String value();
}
