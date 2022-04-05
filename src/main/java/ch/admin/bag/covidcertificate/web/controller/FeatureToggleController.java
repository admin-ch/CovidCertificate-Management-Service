package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.response.FeaturesDto;
import ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feature-toggle")
@RequiredArgsConstructor
@Slf4j
public class FeatureToggleController {

    private final FeatureToggleInterceptor featureToggle;

    @GetMapping("/features")
    public FeaturesDto getFeatures() {
        log.info("Call getting all configured features");
        return new FeaturesDto(featureToggle.getFeatures());
    }
}
