package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwissDGCSignerTest {
    private final JFixture fixture = new JFixture();

    @Mock
    private COSEService coseService;

    @InjectMocks
    private SwissDGCSigner swissDGCSigner;

    @Test
    void whenSign_thenOk() {
        // given
        byte[] dgcCBOR = fixture.create(byte[].class);
        when(coseService.getCOSESign1(any(byte[].class), any())).thenReturn(dgcCBOR);
        // when
        byte[] result = swissDGCSigner.sign(fixture.create(byte[].class), Instant.now());
        // then
        assertEquals(dgcCBOR, result);
    }

    @Test
    void whenGetSignerExpiration_thenThrowsUnsupportedOperationException() {
        // when then
        assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerExpiration());
    }

    @Test
    void whenGetSignerCountry_thenThrowsUnsupportedOperationException() {
        // when then
        assertThrows(UnsupportedOperationException.class, () -> swissDGCSigner.getSignerCountry());
    }
}
