package ch.admin.bag.covidcertificate.config.featureToggle;

import ch.admin.bag.covidcertificate.api.request.CertificateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeatureData {

    private List<String> uris;
    private CertificateType type;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;

    public boolean matchesUris(String uri) {
        return uris.stream().anyMatch(u -> uri.matches("^/api/v1/" + u + "/?"));
    }

    @JsonIgnore
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return Range.between(start, end).contains(now);
    }

}
