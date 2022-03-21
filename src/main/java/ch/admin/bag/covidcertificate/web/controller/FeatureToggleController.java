package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.response.FeaturesDto;
import ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/feature-toggle")
@RequiredArgsConstructor
@Slf4j
public class FeatureToggleController {

    private final FeatureToggleInterceptor featureToggle;
    private final SecurityHelper securityHelper;

    @GetMapping("/features")
    public FeaturesDto getFeatures(HttpServletRequest request) {
        log.info("Call getting all configured features");
        securityHelper.authorizeUser(request);
        return new FeaturesDto(featureToggle.getFeatures());
    }
}
