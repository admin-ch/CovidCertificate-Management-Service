package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.config.featureToggle.FeatureData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeaturesDto {
    private final List<FeatureData> featureData;
}
