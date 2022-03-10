package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ServiceData {

    @NestedConfigurationProperty
    private Map<String,Function> functions = new HashMap<>();

    @Data
    public static class Function {
        private String identifier;
        private List<String> uri;
        private LocalDateTime from;
        private LocalDateTime until;
        private List<String> additionalRef;
        private List<Function> additional;
        private List<String> oneOf;
    }
}
