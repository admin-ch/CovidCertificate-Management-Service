package ch.admin.bag.covidcertificate.testutil;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyPairTestUtil {

    private static final String CRYPTO_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 2048;
    private static final String KEY_ID = "test-id";

    private KeyPair keyPair;

    public KeyPairTestUtil() {
        try {
            this.keyPair = getKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getPrivateKey() {
        return Base64.getEncoder().encodeToString(this.keyPair.getPrivate().getEncoded());
    }

    public String getJwks() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(CRYPTO_ALGORITHM);
        X509EncodedKeySpec spec =  new X509EncodedKeySpec(this.keyPair.getPublic().getEncoded());
        RSAPublicKey publicKeyObj = (RSAPublicKey) keyFactory.generatePublic(spec);
        RSAKey.Builder builder = new RSAKey.Builder(publicKeyObj)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(KEY_ID);
        return new JWKSet(builder.build()).toString();
    }

    private KeyPair getKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CRYPTO_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }
}
