package ch.admin.bag.covidcertificate.service.document.util;

import ch.admin.bag.covidcertificate.service.document.CustomMessageResolver;
import ch.admin.bag.covidcertificate.service.domain.AbstractCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.util.DateHelper;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cc-management-service.pdf.show-watermark}")
    private boolean showWatermark;

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

    public String render(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt) {
        var context = this.getContext(data, barcodeImage, issuedAt);
        return templateEngine.process("pdf", context);
    }

    private Context getContext(AbstractCertificatePdf data, String barcodeImage, LocalDateTime issuedAt) {
        var context = new Context();
        context.setLocale(this.getLocale(data.getLanguage()));
        context.setVariable("isEvidence", false);
        context.setVariable("showWatermark", this.showWatermark);
        context.setVariable("qrCode", barcodeImage);
        context.setVariable("dateFormatter", LOCAL_DATE_FORMAT);
        context.setVariable("creationDate", issuedAt.format(LOCAL_DATE_FORMAT));
        context.setVariable("creationTime", issuedAt.format(DateTimeFormatter.ofPattern("HH:mm")));
        context.setVariable("birthdate", DateHelper.formatDateOfBirth(data.getDateOfBirth()));

        // set data in correct type
        if (data instanceof VaccinationCertificatePdf) {
            context.setVariable("data", (VaccinationCertificatePdf) data);
            context.setVariable("type", "vaccine");
            var isEvidence = ((VaccinationCertificatePdf) data).getNumberOfDoses() < ((VaccinationCertificatePdf) data).getTotalNumberOfDoses();
            context.setVariable("isEvidence", isEvidence);
        } else if (data instanceof RecoveryCertificatePdf) {
            context.setVariable("data", (RecoveryCertificatePdf) data);
            context.setVariable("type", "recovery");
        } else {
            context.setVariable("data", (TestCertificatePdf) data);
            context.setVariable("type", "test");
            context.setVariable("dateTimeFormatter", DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"));
        }
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
