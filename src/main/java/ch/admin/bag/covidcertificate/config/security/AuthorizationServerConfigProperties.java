package ch.admin.bag.covidcertificate.config.security;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Configuration properties to configure the authorization server that the OAuth2 resource server will accept tokens from.
 */
@Data
public class AuthorizationServerConfigProperties {

    private static final String JWK_SET_URI_SUBPATH = "/protocol/openid-connect/certs";

    private String issuer;

    private String jwkSetUri;

    public String getJwkSetUri() {
        return StringUtils.isNotBlank(jwkSetUri) ? jwkSetUri : issuer + JWK_SET_URI_SUBPATH;
    }
}

