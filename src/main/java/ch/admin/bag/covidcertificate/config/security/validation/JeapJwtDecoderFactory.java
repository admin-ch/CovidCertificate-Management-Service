package ch.admin.bag.covidcertificate.config.security.validation;

import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

public class JeapJwtDecoderFactory {

    public static JwtDecoder createJwtDecoder(String authorizationServerJwkSetUri, OAuth2TokenValidator<Jwt> jwtValidator) {
        return createDefaultJwtDecoder(authorizationServerJwkSetUri, jwtValidator);
    }

    private static JwtDecoder createDefaultJwtDecoder(String jwkSetUri, OAuth2TokenValidator<Jwt> jwtValidator) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.
                withJwkSetUri(jwkSetUri).
                jwsAlgorithm(SignatureAlgorithm.RS256).
                jwsAlgorithm(SignatureAlgorithm.RS512).
                build();
        jwtDecoder.setJwtValidator(jwtValidator);
        return jwtDecoder;
    }

}
