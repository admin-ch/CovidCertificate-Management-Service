package ch.admin.bag.covidcertificate.api.parsing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class StringNotEmptyToUppercaseElseNullDeserializer extends StdDeserializer<String> {
    public StringNotEmptyToUppercaseElseNullDeserializer() {
        this(null);
    }

    public StringNotEmptyToUppercaseElseNullDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String appCode = jp.getCodec().readValue(jp, String.class);

        return StringUtils.hasText(appCode) ? StringUtils.trimAllWhitespace(appCode).toUpperCase() : null;
    }
}
