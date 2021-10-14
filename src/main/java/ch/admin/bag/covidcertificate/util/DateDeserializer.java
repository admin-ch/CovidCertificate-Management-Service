package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static ch.admin.bag.covidcertificate.api.Constants.*;

public class DateDeserializer extends JsonDeserializer<LocalDate> {

    static final String vaccinationCertificate = VaccinationCertificateDataDto.class.getSimpleName();
    static final String recoveryCertificate = RecoveryCertificateDataDto.class.getSimpleName();
    static final String testCertificate = TestCertificateDataDto.class.getSimpleName();

    @Override
    public LocalDate deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String dateAsString = jsonparser.getText();
        var origin = jsonparser.getParsingContext().getCurrentValue().getClass().getSimpleName();

        try {
            return LocalDate.parse(dateAsString);
        } catch (DateTimeParseException e) {
            if (vaccinationCertificate.equals(origin)) {
                throw new CreateCertificateException(INVALID_VACCINATION_DATE);
            } else if(recoveryCertificate.equals(origin)) {
                throw new CreateCertificateException(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT);
            } else if(testCertificate.equals(origin)) {
                throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
            }
            throw e;
        }
    }
}
