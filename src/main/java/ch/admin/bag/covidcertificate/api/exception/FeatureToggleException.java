package ch.admin.bag.covidcertificate.api.exception;

import lombok.Getter;
import org.springframework.core.NestedRuntimeException;

/**
 * Falls ein Feature nicht konfiguriert ist, muss dieser im
 * {@link ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor}
 * bzw. im application.yaml unter cc-management-service.feature-toggle konfiguriert werden.
 */
@Getter
public class FeatureToggleException extends NestedRuntimeException {
    private final FeatureToggleError error;

    public FeatureToggleException(FeatureToggleError error, Object... objects) {
        super(String.format(error.getErrorMessage(), objects));
        this.error = new FeatureToggleError(error.getErrorCode(), String.format(error.getErrorMessage(), objects), error.getHttpStatus());
    }

}
