package ch.admin.bag.covidcertificate.service.document.util;

import ch.admin.bag.covidcertificate.service.document.CustomMessageResolver;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;

public class PdfHtmlRenderer {

    private final TemplateEngine templateEngine;

    public PdfHtmlRenderer() {
        this.templateEngine = this.getTemplateEngine();
    }

    private TemplateEngine getTemplateEngine() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        var templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateEngine.setMessageResolver(new CustomMessageResolver());
        return templateEngine;
    }

    public String render(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt, boolean showWatermark) {
        var context = this.getContext(data, barcodeImage, issuedAt, showWatermark);
        return templateEngine.process("pdf", context);
    }

    private Context getContext(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt, boolean showWatermark) {
        var context = new Context();
        context.setLocale(this.getLocale(data.getLanguage()));
        context.setVariable("data", data);
        context.setVariable("isEvidence", data.isEvidence());
        context.setVariable("showWatermark", showWatermark);
        context.setVariable("qrCode", barcodeImage);
        context.setVariable("dateFormatter", LOCAL_DATE_FORMAT);
        context.setVariable("creationDate", issuedAt.format(LOCAL_DATE_FORMAT));
        context.setVariable("creationTime", issuedAt.format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("birthdate", DateHelper.formatDateOfBirth(data.getDateOfBirth()));
        context.setVariable("dateTimeFormatter", DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        context.setVariable("type", data.getType());

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

}
