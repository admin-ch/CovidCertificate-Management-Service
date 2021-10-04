package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.digg.dgc.encoding.Barcode;
import se.digg.dgc.encoding.BarcodeException;
import se.digg.dgc.encoding.impl.DefaultBarcodeCreator;
import se.digg.dgc.service.DGCBarcodeEncoder;

import java.io.IOException;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_BARCODE_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_SIGNATURE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BarcodeServiceTest {
    @Mock
    private SwissDGCBarcodeEncoder dgcBarcodeEncoder;

    @InjectMocks
    private BarcodeService barcodeService;

    private final JFixture fixture = new JFixture();

    @Test
    void whenCreateBarcode_thenOk() throws Exception {
        // given
        Barcode barcode = getBarcode();
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class), any()))
                .thenReturn(barcode);
        // when
        Barcode result = barcodeService.createBarcode("{\"hello\": \"world\"}", fixture.create(SigningInformation.class));
        // then
        assertEquals(barcode, result);
    }

    @Test
    void givenCreateCertificateExceptionIsThrown_whenCreateBarcode_thenThrowsThisException() throws Exception {
        // given
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class), any(SigningInformation.class)))
                .thenThrow(new CreateCertificateException(CREATE_SIGNATURE_FAILED));
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> barcodeService.createBarcode("{\"hello\": \"world\"}", fixture.create(SigningInformation.class)));
        assertEquals(CREATE_SIGNATURE_FAILED, exception.getError());
    }

    @Test
    void givenExceptionIsThrown_whenCreateBarcode_thenThrowsBarcodeError() throws Exception {
        // given
        when(dgcBarcodeEncoder.encodeToBarcode(any(byte[].class), any(Instant.class), any()))
                .thenThrow(IOException.class);
        // when then
        CreateCertificateException exception = assertThrows(CreateCertificateException.class,
                () -> barcodeService.createBarcode("{\"hello\": \"world\"}", fixture.create(SigningInformation.class)));
        assertEquals(CREATE_BARCODE_FAILED, exception.getError());
    }

    private Barcode getBarcode() throws BarcodeException {
        var barcodeCreator = new DefaultBarcodeCreator();
        return barcodeCreator.create("Hello world");
    }
}
