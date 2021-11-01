package ch.admin.bag.covidcertificate.service.document;

import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;

import java.util.Arrays;
import java.util.Locale;

public class CustomMessageResolver extends AbstractMessageResolver {

    private final MessageSource messageSource;

    public CustomMessageResolver() {
        this.messageSource = this.messageSource();
    }

    private ResourceBundleMessageSource messageSource() {
        var source = new ResourceBundleMessageSource();
        source.setBasenames("templates/messages/document-messages");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Override
    public String resolveMessage(ITemplateContext iTemplateContext, Class<?> aClass, String s, Object[] objects) {
        if (Arrays.asList(objects).contains("en")) {
            return messageSource.getMessage(s, objects, Locale.ENGLISH);
        }
        return messageSource.getMessage(s, objects, iTemplateContext.getLocale());
    }

    @Override
    public String createAbsentMessageRepresentation(ITemplateContext iTemplateContext, Class<?> aClass, String s, Object[] objects) {
        return null;
    }
}
