package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class ServiceData {

    @NestedConfigurationProperty
    private List<Function> functions = Collections.emptyList();

    @Data
    public static class Function {
        private String identifier;
        private LocalDateTime from;
        private LocalDateTime until;
        private String mandatory;
        private List<String> oneOf;
    }
}
