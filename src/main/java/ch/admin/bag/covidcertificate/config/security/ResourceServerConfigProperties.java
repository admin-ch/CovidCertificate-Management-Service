package ch.admin.bag.covidcertificate.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to configure the OAuth2 resource server.
 */
@Setter
@Getter
@ConfigurationProperties("jeap.security.oauth2.resourceserver")
public class ResourceServerConfigProperties {
    private String resourceId;
    private AuthorizationServerConfigProperties authorizationServer;
}
