package ch.admin.bag.covidcertificate.service.document;

import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;

@Service
@Slf4j
public class PdfCertificateGenerationService {

    private final PdfRendererBuilder pdfBuilder;
    private final boolean showWatermark;

    public PdfCertificateGenerationService(ConfigurableEnvironment env) {
        this.pdfBuilder = getPdfBuilder();
        this.showWatermark = Arrays.stream(env.getActiveProfiles()).noneMatch("prod"::equals);
    }

    private PdfRendererBuilder getPdfBuilder() {
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
            var barcodeImage = this.getBarcodeImage(barcodePayload);
            var content = this.parseThymeleafTemplate("pdf", this.getContext(data), barcodeImage, issuedAt);

            var os = new ByteArrayOutputStream();
            pdfBuilder.toStream(os);
            pdfBuilder.withHtmlContent(content, Objects.requireNonNull(templatePath).toString());
            pdfBuilder.run();

            return os.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String parseThymeleafTemplate(String templateName, Context context, String barcodePayload, LocalDateTime issuedAt) {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        var templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new CustomMessageResolver());

        context.setVariable("qrCode", barcodePayload);
        context.setVariable("dateFormatter", LOCAL_DATE_FORMAT);
        context.setVariable("creationDate", issuedAt.format(LOCAL_DATE_FORMAT));
        context.setVariable("creationTime", issuedAt.format(DateTimeFormatter.ofPattern("HH:mm")));

        return templateEngine.process(templateName, context);
    }

    private Context getContext(AbstractCertificatePdf data) {
        var context = new Context();
        context.setLocale(this.getLocale(data.getLanguage()));
        context.setVariable("isEvidence", false);
        context.setVariable("showWatermark", this.showWatermark);
        context.setVariable("data", data);
        context.setVariable("type", data.getType());
        context.setVariable("dateTimeFormatter", DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        context.setVariable("isEvidence", data.isEvidence());
        return context;
    }

    private Locale getLocale(String language) {
        switch (language) {
            case "fr":
                return Locale.FRENCH;
            case "it":
                return Locale.ITALIAN;
            case "rm":
                return Locale.forLanguageTag("rm");
            default:
                return Locale.GERMAN;
        }
    }

    private String getBarcodeImage(String barcodeContent) throws IOException {
        // Create QR code object with error correction level "M" (up to 15% damage)
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 0);
        BarcodeQRCode qrCode = new BarcodeQRCode(barcodeContent, hints, 300, 300);

        return qrCode.getBase64Barcode();
    }
}
