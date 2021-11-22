package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.service.document.util.PdfHtmlRenderer;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class PdfCertificateGenerationService {

    private File fontArial500;
    private File fontArialBold600;
    private File fontArialItalic500;
    private URL templatePath;
    private PdfHtmlRenderer pdfHtmlRenderer;

    @Value("#{new Boolean('${cc-management-service.pdf.show-watermark}')}")
    private boolean showWatermark;

    @PostConstruct
    public void postConstruct() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        this.templatePath = this.getClass().getClassLoader().getResource("templates/pdf.html");
        this.fontArial500 = new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/arial.ttf")).getPath());
        this.fontArialBold600 = new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/arialbd.ttf")).getPath());
        this.fontArialItalic500 = new File(Objects.requireNonNull(classLoader.getResource("templates/fonts/ariali.ttf")).getPath());
        this.pdfHtmlRenderer = new PdfHtmlRenderer(this.showWatermark);
    }

    private PdfRendererBuilder createPdfBuilder() {
        var builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.usePdfUaAccessbility(true);
        builder.useFont(fontArial500, "Arial", 500, BaseRendererBuilder.FontStyle.NORMAL, false);
        builder.useFont(fontArialBold600, "Arial", 600, BaseRendererBuilder.FontStyle.NORMAL, true);
        builder.useFont(fontArialItalic500, "Arial", 500, BaseRendererBuilder.FontStyle.ITALIC, true);
        builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_U);
        return builder;
    }

    public byte[] generateCovidCertificate(AbstractCertificatePdf data, String barcodePayload, LocalDateTime issuedAt) {
        try {
            var content = pdfHtmlRenderer.render(data, this.getBarcodeImage(barcodePayload), issuedAt);
            var os = new ByteArrayOutputStream();
            PdfRendererBuilder pdfBuilder = createPdfBuilder();
            pdfBuilder.toStream(os);
            pdfBuilder.withHtmlContent(content, Objects.requireNonNull(this.templatePath).toString());
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
