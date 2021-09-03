package ch.admin.bag.covidcertificate.service.document;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfTemplate;

import java.util.Map;

public class BarcodeQRCode {
    private final BitMatrix bm;

    public BarcodeQRCode(String code, Map<EncodeHintType, Object> hints) {
        try {
            var qc = new QRCodeWriter();
            bm = qc.encode(code, BarcodeFormat.QR_CODE, 1, 1, hints);
        } catch (WriterException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex.getCause());
        }
    }

    public Rectangle getBarcodeSize() {
        return new Rectangle(0, 0, bm.getWidth(), bm.getHeight());
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
