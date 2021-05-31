package ch.admin.bag.covidcertificate.config.security.authentication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;


@Slf4j
public class JeapAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String USER_ROLES_CLAIM = "userroles";

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        return new JeapAuthenticationToken(jwt, extractUserRoles(jwt));
    }

    private Set<String> extractUserRoles(Jwt jwt) {
        List userrolesClaim = Optional.of(jwt)
                .map(Jwt::getClaims)
                .flatMap(map -> getIfPossible(map, USER_ROLES_CLAIM, List.class))
                .orElse(Collections.emptyList());

        Set<String> userRoles = new HashSet<>();
        userrolesClaim.forEach( userroleObject -> {
            try {
                userRoles.add((String) userroleObject);
            } catch (ClassCastException e) {
                log.warn("Ignoring non String user role.");
            }
        });

        return userRoles;
    }

    private <T> Optional<T> getIfPossible(Map map, String key, Class<T> klass) {
        Object value = map.get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(klass.cast(value));
        } catch (ClassCastException e) {
            log.warn("Unable to map value of entry {} to class {}, ignoring the entry.", key, klass.getSimpleName());
            return Optional.empty();
        }
    }
}
