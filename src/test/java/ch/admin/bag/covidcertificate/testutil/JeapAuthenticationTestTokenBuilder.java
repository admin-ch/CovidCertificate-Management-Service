package ch.admin.bag.covidcertificate.testutil;

import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationContext;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

// @jEAP team: This builder does not follow the same conventions as the Lombok builders usually used by jEAP.
// -> when breaking changes are introduced, adapt this builder to the Lombok style. See also JwsBuilder.
/**
 * This class can simplify the construction of a JeapAuthenticationToken instance for the most common use cases in unit and integration tests.
 * This class implements a builder pattern. You can start building with an empty JWT (create()) and add claims and roles to it. Or you can start
 * with a given JWT (createWithJwt()) and then add roles to it.
 */
public class JeapAuthenticationTestTokenBuilder {

    private Jwt jwt;
    private Map<String, Object> claims;
    private Set<String> userRoles;
    private Map<String, Set<String>> businessPartnerRoles;

    private JeapAuthenticationTestTokenBuilder(Jwt jwt) {
        this.userRoles = new HashSet<>();
        this.businessPartnerRoles = new HashMap<>();
        this.claims = new HashMap<>();
        this.jwt = jwt;
    }

    public static JeapAuthenticationTestTokenBuilder create() {
        return new JeapAuthenticationTestTokenBuilder(null);
    }

    public static JeapAuthenticationTestTokenBuilder createWithJwt(Jwt jwt) {
        return new JeapAuthenticationTestTokenBuilder(jwt);
    }

    public JeapAuthenticationTestTokenBuilder withContext(JeapAuthenticationContext context) {
        return withClaim(JeapAuthenticationContext.getContextJwtClaimName(), context);
    }

    public JeapAuthenticationTestTokenBuilder withName(String name) {
        return withClaim("name", name);
    }

    public JeapAuthenticationTestTokenBuilder withGivenName(String givenName) {
        return withClaim("given_name", givenName);
    }

    public JeapAuthenticationTestTokenBuilder withFamilyName(String familyName) {
        return withClaim("family_name", familyName);
    }

    public JeapAuthenticationTestTokenBuilder withLocale(String locale) {
        return withClaim("locale", locale);
    }

    public JeapAuthenticationTestTokenBuilder withSubject(String subject) {
        return withClaim("sub", subject);
    }

    public JeapAuthenticationTestTokenBuilder withClaim(String claimName, Object claimValue) {
        checkNoTokenProvided();
        claims.put(claimName, claimValue);
        return this;
    }

    public JeapAuthenticationTestTokenBuilder withUserRoles(String... roles) {
        userRoles.addAll(setOf(roles));
        return this;
    }

    public JeapAuthenticationTestTokenBuilder withBusinessPartnerRoles(String businessPartner, String... roles) {
        Set<String> currentRoles = businessPartnerRoles.get(businessPartner);
        if (currentRoles == null) {
            currentRoles = new HashSet<>();
            businessPartnerRoles.put(businessPartner, currentRoles);
        }
        currentRoles.addAll(setOf(roles));
        return this;
    }

    public JeapAuthenticationToken build() {
        if (jwt != null) {
            return new JeapAuthenticationToken(jwt, userRoles);
        }
        else {
            Jwt.Builder jwtBuilder = createDefaultJwt();
            claims.entrySet().stream().
                    forEach(entry -> jwtBuilder.claim(entry.getKey(), entry.getValue()));
            return new JeapAuthenticationToken(jwtBuilder.build(), userRoles);
        }
    }

    private static Jwt.Builder createDefaultJwt() {
        return Jwt.withTokenValue("dummy token value")
                // at least one header needed
                .header("alg", "none")
                // at least one claim needed
                .claim(JeapAuthenticationContext.getContextJwtClaimName(), JeapAuthenticationContext.USER.name());
    }

    private void checkNoTokenProvided() {
        if (jwt != null) {
            throw new IllegalStateException("Token has been set explicitly, unable to add additional token claims.");
        }
    }

    private <E>  Set<E> setOf(E... elements) {
        Set<E> set = new HashSet<>();
        if (elements != null) {
            set.addAll(Arrays.asList(elements));
        }
        return set;
    }

}
