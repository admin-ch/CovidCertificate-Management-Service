package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class COSEServiceTest {
    private final JFixture fixture = new JFixture();
    @InjectMocks
    COSEService coseService;
    byte[] dgcCBOR;
    byte[] protectedHeader;
    byte[] payload;
    byte[] signatureData;
    byte[] coseSign1;
    byte[] signature;
    @Mock
    private CBORService cborService;
    @Mock
    private SigningClient signingClient;

    @BeforeEach
    public void init() throws Exception {
        // given
        dgcCBOR = fixture.create(byte[].class);
        protectedHeader = fixture.create(byte[].class);
        lenient().when(cborService.getProtectedHeader(any())).thenReturn(protectedHeader);
        payload = fixture.create(byte[].class);
        lenient().when(cborService.getPayload(any(byte[].class))).thenReturn(payload);
        signatureData = fixture.create(byte[].class);
        lenient().when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenReturn(signatureData);
        coseSign1 = fixture.create(byte[].class);
        lenient().when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenReturn(coseSign1);
        signature = fixture.create(byte[].class);
        lenient().when(signingClient.create(any(byte[].class), any())).thenReturn(signature);
    }

    @Test
    void whenGetCOSESign1_thenResultIsNotNull() {
        // when
        byte[] result = coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        assertNotNull(result);
    }

    @Test
    void whenGetCOSESign1_thenCBORServiceGetProtectedHeaderIsCalled() throws Exception {
        // when
        coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        verify(cborService).getProtectedHeader(any());
    }

    @Test
    void whenGetCOSESign1_thenCBORServiceGetPayloadIsCalled() {
        // when
        coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        verify(cborService).getPayload(eq(dgcCBOR));
    }

    @Test
    void whenGetCOSESign1_thenCBORServiceGetSignatureDataIsCalled() {
        // when
        coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        verify(cborService).getSignatureData(eq(protectedHeader), eq(payload));
    }

    @Test
    void whenGetCOSESign1_thenSigningClientCreateIsCalled() {
        // when
        coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        verify(signingClient).create(eq(signatureData), any());
    }

    @Test
    void whenGetCOSESign1_thenCBORServiceGetCOSESign1IsCalled() {
        // when
        coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        verify(cborService).getCOSESign1(eq(protectedHeader), eq(payload), eq(signature));
    }

    @Test
    void whenGetCOSESign1_thenResultIsOk() {
        // when
        byte[] result = coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class));
        // then
        assertArrayEquals(result, coseSign1);
    }

    @Test
    void givenExceptionInCBORServiceGetProtectedHeaderIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() throws Exception {
        // given
        when(cborService.getProtectedHeader(any())).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class)));
        assertEquals(CREATE_COSE_PROTECTED_HEADER_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetPayloadIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        when(cborService.getPayload(any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class)));
        assertEquals(CREATE_COSE_PAYLOAD_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetSignatureDataIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        when(cborService.getSignatureData(any(byte[].class), any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class)));
        assertEquals(CREATE_COSE_SIGNATURE_DATA_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInSigningClientCreateIsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        when(signingClient.create(any(byte[].class), any())).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class)));
        assertEquals(CREATE_SIGNATURE_FAILED, exception.getError());
    }

    @Test
    void givenExceptionInCBORServiceGetCOSESign1IsThrown_whenGetCOSESign1_thenThrowsCreateCertificateException() {
        // given
        when(cborService.getCOSESign1(any(byte[].class), any(byte[].class), any(byte[].class))).thenThrow(IllegalArgumentException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> coseService.getCOSESign1(dgcCBOR, fixture.create(SigningInformation.class)));
        assertEquals(CREATE_COSE_SIGN1_FAILED, exception.getError());
    }
}
