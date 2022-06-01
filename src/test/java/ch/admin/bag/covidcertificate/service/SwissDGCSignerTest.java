package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwissDGCSignerTest {

    private final JFixture fixture = new JFixture();

    @Mock
    private COSEService coseService;

    @InjectMocks
    private SwissDGCSigner swissDGCSigner;

    @BeforeEach
    private void init(){
        lenient().when(coseService.getCOSESign1(any(), any(), any())).thenReturn(fixture.create(byte[].class));
    }

    @Nested
    class Sign{
        @Test
        void callsCoseServiceWithCorrectCbor() {
            var dgcCBOR = fixture.create(byte[].class);
            swissDGCSigner.sign(dgcCBOR, fixture.create(SigningInformationDto.class), fixture.create(Instant.class));
            verify(coseService).getCOSESign1(eq(dgcCBOR), any(), any());
        }

        @Test
        void callsCoseServiceWithCorrectSigningInformation() {
            var signingInformation = fixture.create(SigningInformationDto.class);
            byte[] result = swissDGCSigner.sign(fixture.create(byte[].class), signingInformation,
                                                fixture.create(Instant.class));
            verify(coseService).getCOSESign1(any(), eq(signingInformation), any());
        }

        @Test
        void callsCoseServiceWithCorrectExpirationInstant() {
            var expirationInstant = fixture.create(Instant.class);
            byte[] result = swissDGCSigner.sign(fixture.create(byte[].class),
                                                fixture.create(SigningInformationDto.class), expirationInstant);
            verify(coseService).getCOSESign1(any(), any(), eq(expirationInstant));
        }

        @Test
        void returnsCoseObject() {
            var dgcCBOR = fixture.create(byte[].class);
            when(coseService.getCOSESign1(any(byte[].class), any(), any(Instant.class))).thenReturn(dgcCBOR);

            var result = swissDGCSigner.sign(fixture.create(byte[].class), Instant.now());

            assertEquals(dgcCBOR, result);
        }

        @Test
        void whenGetSignerExpiration_thenThrowsUnsupportedOperationException() {
            assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerExpiration());
        }

        @Test
        void whenGetSignerCountry_thenThrowsUnsupportedOperationException() {
            assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerCountry());
        }
    }

}
