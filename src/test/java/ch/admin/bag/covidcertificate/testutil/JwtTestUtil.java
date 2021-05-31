package ch.admin.bag.covidcertificate.testutil;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JwtTestUtil {
    private static final String JWT_CONTEXT = "USER";
    private static final String FIRST_NAME = "Henriette";
    private static final String LAST_NAME = "Muster";
    private static final String PREFERRED_USERNAME = "12345";
    private static final String LOCALE_DE = "DE";
    private static final String CLIENT_ID = "ha-ui";
    private static final String CRYPTO_ALGORITHM = "RSA";
    private static final String ISSUER = "http://localhost:8180";

    public static String getJwtTestToken(String privateKey, LocalDateTime expiration, String userRole) throws Exception {
        KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        KeyFactory kf = KeyFactory.getInstance(CRYPTO_ALGORITHM);
        PrivateKey privateKeyToSignWith = kf.generatePrivate(keySpec);
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("user_name", "user");
        claims.put("ctx", JWT_CONTEXT);
        claims.put("iss", ISSUER);
        claims.put("preferred_username", PREFERRED_USERNAME);
        claims.put("given_name", FIRST_NAME);
        claims.put("locale", LOCALE_DE);
        claims.put("client_id", CLIENT_ID);
        claims.put("bproles", new HashMap<>());
        claims.put("userroles", Collections.singletonList(userRole));
        claims.put("scope", Arrays.asList("email", "openid", "profile"));
        claims.put("name", FIRST_NAME + " " + LAST_NAME);
        claims.put("exp", convertToDateViaInstant(expiration));
        claims.put("family_name", LAST_NAME);
        claims.put("jti", UUID.randomUUID().toString());

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(ISSUER)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(UUID.randomUUID().toString())
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(privateKeyToSignWith, SignatureAlgorithm.RS256)
                .compact();
    }

    private static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
