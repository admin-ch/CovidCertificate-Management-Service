package ch.admin.bag.covidcertificate.authorization.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ConfigurationPropertiesBinding
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String timestamp) {
        return LocalDateTime.parse(timestamp);
    }
}