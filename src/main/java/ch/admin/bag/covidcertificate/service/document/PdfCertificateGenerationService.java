package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.service.document.util.PdfHtmlRenderer;
import ch.admin.bag.covidcertificate.service.domain.pdf.AbstractCertificatePdf;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PdfCertificateGenerationService {

    @Value("#{new Boolean('${cc-management-service.pdf.show-watermark}')}")
    private boolean showWatermark;

    private PdfRendererBuilder createPdfBuilder() {
        var classLoader = this.getClass().getClassLoader();
        var builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.usePdfUaAccessbility(true);
        builder.useFont(new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/arial.ttf")).getPath()), "Arial", 500, BaseRendererBuilder.FontStyle.NORMAL, false);
        builder.useFont(new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/arialbd.ttf")).getPath()), "Arial", 600, BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/ariali.ttf")).getPath()), "Arial", 500, BaseRendererBuilder.FontStyle.ITALIC, true);
        builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_U);
        return builder;
    }

    public byte[] generateCovidCertificate(AbstractCertificatePdf data, String barcodePayload, LocalDateTime issuedAt) {
        try {
            var templatePath = this.getClass().getClassLoader().getResource("templates/pdf.html");
            PdfHtmlRenderer pdfHtmlRenderer = new PdfHtmlRenderer(this.showWatermark);
            var content = pdfHtmlRenderer.render(data, this.getBarcodeImage(barcodePayload), issuedAt);

            var os = new ByteArrayOutputStream();
            PdfRendererBuilder pdfBuilder = createPdfBuilder();
            pdfBuilder.toStream(os);
            pdfBuilder.withHtmlContent(content, Objects.requireNonNull(templatePath).toString());
            pdfBuilder.run();

            return os.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String getBarcodeImage(String barcodeContent) throws IOException {
        // Create QR code object with error correction level "M" (up to 15% damage)
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 0);
        BarcodeQRCode qrCode = new BarcodeQRCode(barcodeContent, hints, 1000, 1000);

        return qrCode.getBase64Barcode();
    }

}
