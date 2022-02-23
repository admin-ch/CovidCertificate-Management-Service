package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
public class RoleData {

    private String intern;
    private String eiam;
    private String claim;
}
