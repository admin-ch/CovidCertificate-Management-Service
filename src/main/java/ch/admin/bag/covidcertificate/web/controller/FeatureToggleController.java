package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.config.featureToggle.FeatureData;
import ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class FeatureToggleController {
    private final FeatureToggleInterceptor featureToggle;

    @GetMapping("/features")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<FeatureData> getFeatures() {
        return featureToggle.getFeatures();
    }
}
