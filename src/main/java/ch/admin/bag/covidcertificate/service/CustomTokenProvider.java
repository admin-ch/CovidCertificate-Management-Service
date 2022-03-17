package ch.admin.bag.covidcertificate.service;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static ch.admin.bag.covidcertificate.api.Constants.USER_EXT_ID_CLAIM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.USER_ROLES_CLAIM_KEY;

@Component
@Slf4j
public class CustomTokenProvider {

    private static final String COVID_CERT_CREATION = "covidcertcreation";
    private static final String SCOPE_CLAIM_KEY = "scope";
    private static final String IDP_SOURCE_CLAIM_KEY = "idpsource";
    private static final String TYP_CLAIM_KEY = "typ";
    private static final String AUTH_MACHINE_JWT = "authmachine+jwt";

    @Value("${cc-management-service.jwt.token-validity}")
    private long tokenValidity;

    @Value("${cc-management-service.jwt.issuer}")
    private String issuer;

    @Value("${cc-management-service.jwt.privateKey}")
    private String privateKey;

    private Key signingKey;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        final KeyFactory rsa = KeyFactory.getInstance("RSA");

        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Decoders.BASE64.decode(privateKey));

        try {
            signingKey = rsa.generatePrivate(spec);
        } catch (InvalidKeySpecException e) {
            log.error("Error during generate private key", e);
            throw new IllegalStateException(e);
        }
    }

    public String createToken(String userExtId, String idpSource, List<String> userRoles) {
        log.info("CreateToken with userExtId {} and idpSource {} and userRoles {}", userExtId, idpSource, userRoles);

        if (StringUtil.isBlank(userExtId) || StringUtil.isBlank(idpSource) || CollectionUtils.isEmpty(userRoles))  {
            throw new IllegalStateException("UserExtId, idpSource and userRoles are required!");
        }

        final long nowMillis = System.currentTimeMillis();
        final Date now = new Date(nowMillis);

        final JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuer(issuer)
                .setIssuedAt(now)
                .setNotBefore(now)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .claim(SCOPE_CLAIM_KEY, COVID_CERT_CREATION)
                .claim(USER_EXT_ID_CLAIM_KEY, userExtId)
                .claim(IDP_SOURCE_CLAIM_KEY, idpSource)
                .claim(USER_ROLES_CLAIM_KEY, userRoles)
                .claim(TYP_CLAIM_KEY, AUTH_MACHINE_JWT)
                .signWith(signingKey, SignatureAlgorithm.RS256);

        builder.setExpiration(new Date(nowMillis + tokenValidity));
        return builder.compact();
    }

}
