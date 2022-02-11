package ch.admin.bag.covidcertificate.config.featureToggle;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FeatureData {
    private String uri;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;

    public boolean matchesUri(String uri) {
        return uri.matches("^/api/v1/" + this.uri + "/?");
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(start) && now.isBefore(end);
    }
}
