package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "services")
public class AuthorizationConfig {

    private ServiceData webUi;
    private ServiceData apiGateway;
    private ServiceData management;
}
