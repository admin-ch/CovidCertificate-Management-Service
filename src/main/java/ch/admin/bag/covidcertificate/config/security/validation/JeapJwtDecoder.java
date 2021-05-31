package ch.admin.bag.covidcertificate.config.security.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@RequiredArgsConstructor
class JeapJwtDecoder implements JwtDecoder {

    private final JwtDecoder authenticationServerJwtDecoder;

    @Override
    public Jwt decode(String token) {
        return authenticationServerJwtDecoder.decode(token);
    }

}
