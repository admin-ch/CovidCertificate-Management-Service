package ch.admin.bag.covidcertificate.config;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Junit 5 extension that populates the SpringSecurityContext with a certain Authentication instance before executing the test method.
 * The authentication used to populate the context is created by executing the factory method specified by the WithAuthentication annotation.
 * If the WithAuthentication annotation is missing on the test method the security context is left unchanged.
 */
public class WithAuthenticationExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(InvocationInterceptor.Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        Optional<WithAuthentication> withAuthenticationAnnotation = getWithAuthenticationAnnotation(extensionContext);
        if (!withAuthenticationAnnotation.isPresent()) {
            invocation.proceed();
        }
        else {
            Authentication authenticationToSet = getAuthentication(extensionContext, withAuthenticationAnnotation.get().value());
            SecurityContext securityContext = getSecurityContext();
            Authentication previousAuthentication = securityContext.getAuthentication();
            securityContext.setAuthentication(authenticationToSet);
            try {
                invocation.proceed();
            } finally {
                securityContext.setAuthentication(previousAuthentication);
            }
        }
    }

    private SecurityContext getSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            securityContext = SecurityContextHolder.createEmptyContext();
            SecurityContextHolder.setContext(securityContext);
        }
        return securityContext;
    }

    private Optional<WithAuthentication> getWithAuthenticationAnnotation(ExtensionContext extensionContext) {
        return AnnotationUtils.findAnnotation(extensionContext.getElement(), WithAuthentication.class);
    }

    private Authentication getAuthentication(ExtensionContext extensionContext, String authenticationFactoryMethodName)  {
        Object authenticationFactoryMethodResult =  executeAuthenticationFactoryMethod(extensionContext.getRequiredTestInstance(), authenticationFactoryMethodName);
        if ( (authenticationFactoryMethodResult != null) && !(authenticationFactoryMethodResult instanceof Authentication) ) {
            throw new IllegalArgumentException("Authentication factory method with name '" + authenticationFactoryMethodName + "' did not produce an object of type Authentication.");
        }
        else {
            return (Authentication) authenticationFactoryMethodResult;
        }
    }

    @SuppressWarnings("java:S3011") // We explicitly want to support non public authentication factory methods for the test setup
    private Object executeAuthenticationFactoryMethod(Object testInstance, String authenticationFactoryMethodName) {
        try  {
            Method authenticationFactoryMethod = testInstance.getClass().getDeclaredMethod(authenticationFactoryMethodName);
            // allow the execution of authentication factory methods with lesser visibility than public
            authenticationFactoryMethod.setAccessible(true);
            return authenticationFactoryMethod.invoke(testInstance);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No authentication factory method with name '" + authenticationFactoryMethodName + "' found in test class.", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Unable to execute authentication factory method with name '" + authenticationFactoryMethodName + "'.", e);
        }
    }

}
