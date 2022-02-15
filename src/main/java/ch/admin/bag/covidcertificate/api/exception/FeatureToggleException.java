package ch.admin.bag.covidcertificate.api.exception;

import org.springframework.core.NestedRuntimeException;

/**
 * Falls ein Feature nicht konfiguriert ist, muss dieser im
 * {@link ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor}
 * bzw. im application.yaml unter cc-management-service.feature-toggle konfiguriert werden.
 */
public class FeatureToggleException extends NestedRuntimeException {

    public FeatureToggleException(String path) {
        super(String.format("Feature zur URI %s ist deaktiviert", path));
    }

}
