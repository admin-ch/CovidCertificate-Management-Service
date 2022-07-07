package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser jsonparser, DeserializationContext context) {
        try {
            return jsonparser.getCodec().readValue(jsonparser, ZonedDateTime.class);
        } catch (Exception e) {
            try {
                String dateAsString = jsonparser.getText();
                return ZonedDateTime.parse(dateAsString);
            } catch (Exception ex) {
                throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
            }
        }
    }
}
