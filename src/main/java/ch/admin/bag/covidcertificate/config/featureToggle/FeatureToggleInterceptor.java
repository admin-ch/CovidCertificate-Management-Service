package ch.admin.bag.covidcertificate.config.featureToggle;

import ch.admin.bag.covidcertificate.api.exception.FeatureToggleException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "cc-management-service.feature-toggle")
public class FeatureToggleInterceptor implements HandlerInterceptor {

    @NestedConfigurationProperty
    private List<FeatureData> features = Collections.emptyList();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        Optional<FeatureData> feature = features.stream()
                .filter(f -> f.matchesUri(uri))
                .findFirst();

        if (feature.isPresent() && !feature.get().isActive()) {
            throw new FeatureToggleException(uri);
        }

        return true;
    }
}
