package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import com.upokecenter.cbor.CBORException;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.signatures.cwt.support.CBORInstantConverter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CBORServiceTest {
    private final JFixture fixture = new JFixture();

    @Mock
    private COSETime coseTime;

    @InjectMocks
    CBORService cborService;

    @Nested
    class GetProtectedHeader{
        @Test
        void writesSigningAlgorithmInProtectedHeader() throws Exception {
            String keyIdentifier = Hex.encodeHexString(fixture.create(byte[].class));

            byte[] result = cborService.getProtectedHeader(keyIdentifier);

            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertNotNull(CBORObject.FromObject(1));
            assertEquals(-37, resultCBORObject.get(CBORObject.FromObject(1)).AsInt32());
        }

        @Test
        void writesKeyIdentifierInProtectedHeader() throws Exception {
            String keyIdentifier = Hex.encodeHexString(fixture.create(byte[].class));

            byte[] result = cborService.getProtectedHeader(keyIdentifier);

            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertNotNull(CBORObject.FromObject(4));
            assertArrayEquals(Hex.decodeHex(keyIdentifier), resultCBORObject.get(CBORObject.FromObject(4)).GetByteString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void throwsIllegalArgumentException_ifKeyIdentifierIsNullOrBlank(String keyIdentifier) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getProtectedHeader(keyIdentifier));
            assertTrue(exception.getMessage().toLowerCase().contains("keyidentifier"));
        }

        @Test
        void throwsDecoderException_ifKeyIdentifierIsNotAValidHexString() {
            assertThrows(DecoderException.class,
                    () -> cborService.getProtectedHeader(fixture.create(String.class)));
        }
    }

    @Nested
    class GetPayload{
        private byte[] createHCert() {
            var dgcJson = fixture.create(JSONObject.class).toString();
            return CBORObject.FromJSONString(dgcJson).EncodeToBytes();
        }
        @Test
        void writesCorrectIssuerInThePayload() {
            byte[] result = cborService.getPayload(createHCert(), fixture.create(Instant.class));

            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals("CH", resultCBORObject.get(1).AsString());
        }

        @Test
        void writesCorrectIssuedAtTimestampInThePayload() {
            CBORInstantConverter instantConverter = new CBORInstantConverter();
            Instant issuedAt = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);
            when(coseTime.getIssuedAt()).thenReturn(issuedAt);

            byte[] result = cborService.getPayload(createHCert(), fixture.create(Instant.class));

            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(issuedAt, instantConverter.FromCBORObject(resultCBORObject.get(6)));
        }

        @Test
        void writesCorrectExpirationTimestampInThePayload() {
            CBORInstantConverter instantConverter = new CBORInstantConverter();
            Instant expiration = fixture.create(Instant.class).truncatedTo(ChronoUnit.SECONDS);

            byte[] result = cborService.getPayload(createHCert(), expiration);

            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(expiration, instantConverter.FromCBORObject(resultCBORObject.get(4)));
        }

        @Test
        void writesCorrectHCertInThePayload() {
            byte[] hcert = createHCert();
            // when
            byte[] result = cborService.getPayload(hcert, fixture.create(Instant.class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(hcert, resultCBORObject.get(-260).get(1).EncodeToBytes());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifHCertIsNullOrEmpty(byte[] hcert) {
            var expiredAt = fixture.create(Instant.class);
            // when then
            var exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getPayload(hcert, expiredAt));
            assertTrue(exception.getMessage().toLowerCase().contains("hcert"));
        }

        @Test
        void throwsCBORException_ifHCertIsNotAValidCborObject() {
            var hcert = new byte[] {38, 41, 89};
            var expiredAt = fixture.create(Instant.class);
            assertThrows(CBORException.class,
                    () -> cborService.getPayload(hcert, expiredAt));
        }
    }

    @Nested
    class GetSignatureData{
        @Test
        void createsSignatureDataInCorrectOrderAndWithCorrectData() {
            // given
            byte[] coseProtectedHeader = fixture.create(byte[].class);
            byte[] payload = fixture.create(byte[].class);
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

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifProtectedHeaderIsNullOrEmpty(byte[] protectedHeader) {
            // when then
            byte[] payload = fixture.create(byte[].class);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getSignatureData(protectedHeader, payload));
            assertTrue(exception.getMessage().toLowerCase().contains("protectedheader"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifPayloadIsNullOrEmpty(byte[] payload) {
            // when then
            byte[] bodyProtected = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getSignatureData(bodyProtected, payload));
            assertTrue(exception.getMessage().toLowerCase().contains("payload"));
        }
    }

    @Nested
    class GetCOSESign1{
        @Test
        void writesProtectedHeader() {
            // given
            byte[] coseProtectedHeader = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(coseProtectedHeader, fixture.create(byte[].class), fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(coseProtectedHeader, resultCBORObject.get(0).GetByteString());
        }

        @Test
        void writesUnProtectedHeaderAsAnEmptyMap() {
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), fixture.create(byte[].class), fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertEquals(CBORObject.NewMap(), resultCBORObject.get(1));
        }

        @Test
        void writesPayload() {
            // given
            byte[] payload = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), payload, fixture.create(byte[].class));
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(payload, resultCBORObject.get(2).GetByteString());
        }

        @Test
        void writesSignature() {
            // given
            byte[] signature = fixture.create(byte[].class);
            // when
            byte[] result = cborService.getCOSESign1(fixture.create(byte[].class), fixture.create(byte[].class), signature);
            // then
            assertNotNull(result);
            CBORObject resultCBORObject = CBORObject.DecodeFromBytes(result);
            assertArrayEquals(signature, resultCBORObject.get(3).GetByteString());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifProtectedHeaderIsNullOrEmpty(byte[] protectedHeader) {
            // when then
            byte[] payload = fixture.create(byte[].class);
            byte[] signature = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(protectedHeader, payload, signature));
            assertTrue(exception.getMessage().toLowerCase().contains("protectedheader"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifPayloadIsNullOrEmpty(byte[] payload) {
            // when then
            byte[] protectedHeader = fixture.create(byte[].class);
            byte[] signature = fixture.create(byte[].class);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(protectedHeader, payload, signature));
            assertTrue(exception.getMessage().toLowerCase().contains("payload"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void throwsIllegalArgumentException_ifSignatureIsNullOrEmpty(byte[] signature) {
            // given
            byte[] protectedHeader = fixture.create(byte[].class);
            byte[] payload = fixture.create(byte[].class);
            // when then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> cborService.getCOSESign1(protectedHeader, payload, signature));
            assertTrue(exception.getMessage().toLowerCase().contains("signature"));
        }
    }
}
