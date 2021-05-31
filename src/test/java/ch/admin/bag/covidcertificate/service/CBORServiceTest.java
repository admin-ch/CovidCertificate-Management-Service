package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.digg.dgc.signatures.cwt.support.CBORInstantConverter;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CBORServiceTest {
    private final JFixture jFixture = new JFixture();

    @Mock
    private COSETime coseTime;

    @InjectMocks
    CBORService cborService;

    @Test
    void whenGetProtectedHeader_thenOk() throws Exception {
        // given
        String keyIdentifier = "24BC6B7B7BD2C328";
        ReflectionTestUtils.setField(cborService, "keyIdentifier", keyIdentifier);
        // when
        byte[] result = cborService.getProtectedHeader();
        // then
        assertNotNull(result);
        CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
        assertEquals(-37, resultCBORObject.get(CBORObject.FromObject(1)).AsInt32());
        assertArrayEquals(Hex.decodeHex(keyIdentifier), resultCBORObject.get(CBORObject.FromObject(4)).GetByteString());
    }

    @Test
    void givenKeyIdentifierIsEmpty_whenGetProtectedHeader_thenThrowsIllegalArgumentException() {
        // when then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                cborService::getProtectedHeader);
        assertTrue(exception.getMessage().toLowerCase().contains("keyidentifier"));
    }

    @Test
    void whenGetPayload_thenOk() {
        CBORInstantConverter instantConverter = new CBORInstantConverter();
        // given
        Instant issuedAt = Instant.parse("2021-01-01T00:00:00Z");
        Instant expiration = Instant.parse("2022-01-01T00:00:00Z");
        when(coseTime.getIssuedAt()).thenReturn(issuedAt);
        when(coseTime.getExpiration()).thenReturn(expiration);
        byte[] hcert = CBORObject.FromJSONString("{\"hello\": \"world\"}").EncodeToBytes();
        // when
        byte[] result = cborService.getPayload(hcert);
        // then
        assertNotNull(result);
        CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
        assertEquals("CH BAG", resultCBORObject.get(1).AsString());
        assertEquals(issuedAt, instantConverter.FromCBORObject(resultCBORObject.get(6)));
        assertEquals(expiration, instantConverter.FromCBORObject(resultCBORObject.get(4)));
        assertArrayEquals(hcert, resultCBORObject.get(-260).get(1).EncodeToBytes());
    }

    @Test
    void givenHcertIsEmpty_whenGetPayload_thenThrowsIllegalArgumentException() {
        // when then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getPayload(null));
        assertTrue(exception.getMessage().toLowerCase().contains("hcert"));
    }

    @Test
    void whenGetSignatureData_thenOk() {
        // given
        byte[] coseProtectedHeader = jFixture.create(byte[].class);
        byte[] payload = jFixture.create(byte[].class);
        // when
        byte[] result = cborService.getSignatureData(coseProtectedHeader, payload);
        // then
        assertNotNull(result);
        CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
        assertEquals("Signature1", resultCBORObject.get(0).AsString());
        assertArrayEquals(coseProtectedHeader, resultCBORObject.get(1).GetByteString());
        assertArrayEquals(new byte[0], resultCBORObject.get(2).GetByteString());
        assertArrayEquals(payload, resultCBORObject.get(3).GetByteString());
    }

    @Test
    void givenBodyProtectedIsEmpty_whenGetSignatureData_thenThrowsIllegalArgumentException() {
        // when then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getSignatureData(null, null));
        assertTrue(exception.getMessage().toLowerCase().contains("bodyprotected"));
    }

    @Test
    void givenPayloadIsEmpty_whenGetSignatureData_thenThrowsIllegalArgumentException() {
        // when then
        byte[] bodyProtected = jFixture.create(byte[].class);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getSignatureData(bodyProtected, null));
        assertTrue(exception.getMessage().toLowerCase().contains("payload"));
    }

    @Test
    void whenGetCOSESign1_thenOk() {
        // given
        byte[] coseProtectedHeader = jFixture.create(byte[].class);
        byte[] payload = jFixture.create(byte[].class);
        byte[] signature = jFixture.create(byte[].class);
        // when
        byte[] result = cborService.getCOSESign1(coseProtectedHeader, payload, signature);
        // then
        assertNotNull(result);
        CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
        assertArrayEquals(coseProtectedHeader, resultCBORObject.get(0).GetByteString());
        assertEquals(CBORObject.NewMap(), resultCBORObject.get(1));
        assertArrayEquals(payload, resultCBORObject.get(2).GetByteString());
        assertArrayEquals(signature, resultCBORObject.get(3).GetByteString());
    }

    @Test
    void givenProtectedHeaderIsEmpty_whenGetCOSESign1_thenThrowsIllegalArgumentException() {
        // when then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getCOSESign1(null, null, null));
        assertTrue(exception.getMessage().toLowerCase().contains("protectedheader"));
    }

    @Test
    void givenPayloadIsEmpty_whenGetCOSESign1_thenThrowsIllegalArgumentException() {
        // when then
        byte[] protectedHeader = jFixture.create(byte[].class);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getCOSESign1(protectedHeader, null, null));
        assertTrue(exception.getMessage().toLowerCase().contains("payload"));
    }

    @Test
    void givenSignatureIsEmpty_whenGetCOSESign1_thenThrowsIllegalArgumentException() {
        // given
        byte[] bytesMock = jFixture.create(byte[].class);
        // when then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cborService.getCOSESign1(bytesMock, bytesMock, null));
        assertTrue(exception.getMessage().toLowerCase().contains("signature"));
    }
}
