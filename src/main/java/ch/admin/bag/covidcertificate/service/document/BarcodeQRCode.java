package ch.admin.bag.covidcertificate.service.document;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class BarcodeQRCode {
    private final BitMatrix bm;

    public BarcodeQRCode(String code, Map<EncodeHintType, Object> hints) {
        this(code, hints, 1, 1);
    }

    public BarcodeQRCode(String code, Map<EncodeHintType, Object> hints, int width, int height) {
        try {
            var qc = new QRCodeWriter();
            bm = qc.encode(code, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex.getCause());
        }
    }

    public Rectangle getBarcodeSize() {
        return new Rectangle(0, 0, bm.getWidth(), bm.getHeight());
    }

    public String getBase64Barcode() throws IOException {
        var outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(this.bm,"png", outputStream);

        return new String(Base64.getEncoder().encode(outputStream.toByteArray()));
    }

    public void placeBarcode(PdfTemplate canvas, float moduleSide) {
        final int width = bm.getWidth();
        final int height = bm.getHeight();

        for (var y = 0; y < height; ++y) {
            for (var x = 0; x < width; ++x) {
                if (bm.get(x,y)) {
                    canvas.rectangle(x * moduleSide, (height - y - 1) * moduleSide, moduleSide, moduleSide);
                }
            }
        }
        canvas.fill();
    }
}
