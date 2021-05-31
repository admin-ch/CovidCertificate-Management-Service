package ch.admin.bag.covidcertificate.web.monitoring;


import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class HealthMetricsConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> prometheusHealthCheck(HealthEndpoint healthEndpoint) {
        return registry -> registry.gauge("health", healthEndpoint, HealthMetricsConfig::healthToCode);
    }

    private static int healthToCode(HealthEndpoint ep) {
        Status status = ep.health().getStatus();
        return status.equals(Status.UP) ? 1 : 0;
    }

}
