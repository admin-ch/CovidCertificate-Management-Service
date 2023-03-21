package ch.admin.bag.covidcertificate.config.cleanup;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
public class Cleanup {

    @NestedConfigurationProperty
    private Database database;
    
    @NestedConfigurationProperty
    private SqlQuery sqlQuery;
}
