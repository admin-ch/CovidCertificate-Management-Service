package ch.admin.bag.covidcertificate.service.document.util;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.service.document.CustomMessageResolver;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static ch.admin.bag.covidcertificate.api.Constants.LOCAL_DATE_FORMAT;

@Slf4j
public class PdfHtmlRenderer {

    private final TemplateEngine templateEngine;
    private final boolean showWatermark;

    public PdfHtmlRenderer(boolean showWatermark) {
        this.templateEngine = this.createTemplateEngine();
        this.showWatermark = showWatermark;
    }

    private TemplateEngine createTemplateEngine() {
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        var newTemplateEngine = new TemplateEngine();
        newTemplateEngine.setTemplateResolver(templateResolver);
        newTemplateEngine.setMessageResolver(new CustomMessageResolver());
        return newTemplateEngine;
    }

    public String render(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt) {
        var context = this.getContext(data, barcodeImage, issuedAt, showWatermark);
        int hashBefore = data.hashCode();
        String result = templateEngine.process("pdf", context);

        if (hashBefore != data.hashCode()) {
            log.error("Hash of rendered data before does not match hash after!");
            throw new CreateCertificateException(Constants.CREATE_PDF_FAILED);
        }
        return result;
    }

    private Context getContext(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt, boolean showWatermark) {
        var context = new Context();
        context.setLocale(this.getLocale(data.getLanguage()));
        context.setVariable("data", data);
        context.setVariable("showValidOnlyInSwitzerland", data.showValidOnlyInSwitzerland());
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
