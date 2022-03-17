package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.Constants;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

@Slf4j
public class UVCI {
    private static final String PREFIX = "urn:uvci";
    private static final String VERSION = "01";
    private static final byte[] SALT;

    private static final String REGEX_UVCI = "^urn:uvci:01:CH:[A-Z0-9]{24}$";

    private UVCI() {
        throw new IllegalStateException("Utility class");
    }

    static {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        SALT = salt;
    }

    public static String generateUVCI(String input) {
        long timestamp = System.nanoTime();
        String opaqueString = getOpaqueString(input + timestamp);
        return String.format("%s:%s:%s:%s", PREFIX, VERSION, Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, opaqueString);
    }

    public static boolean isValid(String uvci) {
        return uvci.matches(REGEX_UVCI);
    }

    private static String getOpaqueString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            md.update(SALT);
            byte[] inputHash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // XOR parts of the hash to get a 96bit hash
            int xorBlockSize = 4;
            byte[] reducedHash = new byte[12];
            int insertIndex = 0;
            for (int i = 0; i <= inputHash.length - xorBlockSize; i += xorBlockSize) {
                byte[] current = Arrays.copyOfRange(inputHash, i, i + 4);
                reducedHash[insertIndex] = getXor(current);
                ++insertIndex;
            }

            return encodeHexString(reducedHash).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("Could not find hashing algorithm: ", e);
            return null;
        }
    }

    private static byte getXor(byte[] data) {
        byte temp = data[0];
        for (int i = 1; i < data.length; i++) {
            temp ^= data[i];
        }
        return temp;
    }
}
