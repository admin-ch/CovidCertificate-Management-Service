package ch.admin.bag.covidcertificate.service;

import com.upokecenter.cbor.CBORObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.digg.dgc.signatures.cwt.support.CBORInstantConverter;

@Service
@RequiredArgsConstructor
public class CBORService {
    // COSE_Sign1
    public static final int MESSAGE_TAG = 18;
    // Protected Header
    private static final Integer ALG_CBOR_MAJOR_TYPE = 1;
    private static final Integer KID_CBOR_MAJOR_TYPE = 4;
    private static final Integer SIGNING_ALGORITHM = -37; // RSASSA-PSS with SHA-256
    // Payload
    private static final Integer ISS_CLAIM_KEY = 1;
    private static final Integer IAT_CLAIM_KEY = 6;
    private static final Integer EXP_CLAIM_KEY = 4;
    private static final Integer HCERT_CLAIM_KEY = -260;
    private static final Integer HCERT_INNER_CLAIM_KEY = 1;
    private static final String ISSUER = "CH";
    // Signature Data (Sig_structure)
    private static final String CONTEXT = "Signature1";
    private static final byte[] EXTERNAL_AAD = new byte[0];
    private static final CBORObject UNPROTECTED_HEADER = CBORObject.NewMap();
    // Protected Header
    @Value("${cc-management-service.cose.key-identifier}")
    private String keyIdentifier;

    private final COSETime coseTime;

    public byte[] getProtectedHeader() throws DecoderException {
        if (keyIdentifier == null || keyIdentifier.isBlank()) {
            throw new IllegalArgumentException("KeyIdentifier must not be empty.");
        }
        CBORObject protectedHeaderMap = CBORObject.NewMap();
        protectedHeaderMap.Add(CBORObject.FromObject(ALG_CBOR_MAJOR_TYPE), CBORObject.FromObject(SIGNING_ALGORITHM));
        protectedHeaderMap.Add(CBORObject.FromObject(KID_CBOR_MAJOR_TYPE), CBORObject.FromObject(Hex.decodeHex(keyIdentifier)));
        return protectedHeaderMap.EncodeToBytes();
    }

    public byte[] getPayload(byte[] hcert) {
        if (hcert == null || hcert.length == 0) {
            throw new IllegalArgumentException("Hcert must not be empty.");
        }
        CBORInstantConverter instantConverter = new CBORInstantConverter();
        CBORObject cborObject = CBORObject.NewMap();
        cborObject.Add(ISS_CLAIM_KEY, CBORObject.FromObject(ISSUER));
        cborObject.set(IAT_CLAIM_KEY, instantConverter.ToCBORObject(coseTime.getIssuedAt()));
        cborObject.set(EXP_CLAIM_KEY, instantConverter.ToCBORObject(coseTime.getExpiration()));
        CBORObject hcertMap = CBORObject.NewMap();
        hcertMap.set(HCERT_INNER_CLAIM_KEY, CBORObject.DecodeFromBytes(hcert));
        cborObject.Add(HCERT_CLAIM_KEY, hcertMap);
        return cborObject.EncodeToBytes();
    }

    public byte[] getSignatureData(byte[] bodyProtected, byte[] payload) {
        if (bodyProtected == null || bodyProtected.length == 0) {
            throw new IllegalArgumentException("BodyProtected must not be empty.");
        }
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("Payload must not be empty.");
        }
        CBORObject cborObject = CBORObject.NewArray();
        cborObject.Add(CONTEXT);
        cborObject.Add(bodyProtected);
        cborObject.Add(EXTERNAL_AAD);
        cborObject.Add(payload);
        return cborObject.EncodeToBytes();
    }

    public byte[] getCOSESign1(byte[] protectedHeader, byte[] payload, byte[] signature) {
        if (protectedHeader == null || protectedHeader.length == 0) {
            throw new IllegalArgumentException("ProtectedHeader must not be empty.");
        }
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("Payload must not be empty.");
        }
        if (signature == null || signature.length == 0) {
            throw new IllegalArgumentException("Signature must not be empty.");
        }
        CBORObject cborObject = CBORObject.NewArray();
        cborObject.Add(protectedHeader);
        cborObject.Add(UNPROTECTED_HEADER);
        cborObject.Add(payload);
        cborObject.Add(signature);
        cborObject = CBORObject.FromObjectAndTag(cborObject, MESSAGE_TAG);
        return cborObject.EncodeToBytes();
    }
}
