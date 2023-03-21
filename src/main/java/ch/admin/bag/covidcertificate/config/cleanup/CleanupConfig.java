package ch.admin.bag.covidcertificate.config.cleanup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "cc-management-service.cleanup")
public class CleanupConfig {

    @NestedConfigurationProperty
    private Map<String, Cleanup> spots;
}
