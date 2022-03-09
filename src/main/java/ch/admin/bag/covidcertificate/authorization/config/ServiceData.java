package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
public class ServiceData {

    @NestedConfigurationProperty
    private Map<String,Function> functions = Collections.<String,Function>emptyMap();

    @Data
    public static class Function {
        private String identifier;
        private LocalDateTime from;
        private LocalDateTime until;
        @NestedConfigurationProperty
        private List<Function> additional;
        private List<String> oneOf;
    }
}
