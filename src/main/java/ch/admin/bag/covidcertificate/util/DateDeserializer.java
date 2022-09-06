package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDto;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_EXCEPTIONAL_VALID_FROM_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;

public class DateDeserializer extends JsonDeserializer<LocalDate> {

    static final String VACCINATION_TOURIST_CERTIFICATE = VaccinationTouristCertificateDataDto.class.getSimpleName();
    static final String VACCINATION_CERTIFICATE = VaccinationCertificateDataDto.class.getSimpleName();
    static final String RECOVERY_CERTIFICATE = RecoveryCertificateDataDto.class.getSimpleName();
    static final String ANTIBODY_CERTIFICATE = AntibodyCertificateDataDto.class.getSimpleName();
    static final String TEST_CERTIFICATE = TestCertificateDataDto.class.getSimpleName();
    static final String EXCEPTIONAL_CERTIFICATE = ExceptionalCertificateDataDto.class.getSimpleName();

    @Override
    public LocalDate deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        try {
            return jsonparser.getCodec().readValue(jsonparser, LocalDate.class);
        } catch (InvalidFormatException e) {
            try {
                String dateAsString = jsonparser.getText();
                return LocalDate.parse(dateAsString);
            } catch (DateTimeParseException dateTimeParseException) {
                String origin = jsonparser.getParsingContext().getCurrentValue().getClass().getSimpleName();
                if (VACCINATION_CERTIFICATE.equals(origin) ||
                        VACCINATION_TOURIST_CERTIFICATE.equals(origin)) {
                    throw new CreateCertificateException(INVALID_VACCINATION_DATE);
                } else if (RECOVERY_CERTIFICATE.equals(origin) ||
                        ANTIBODY_CERTIFICATE.equals(origin)) {
                    throw new CreateCertificateException(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT);
                } else if (TEST_CERTIFICATE.equals(origin)) {
                    throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
                } else if (EXCEPTIONAL_CERTIFICATE.equals(origin)) {
                    throw new CreateCertificateException(INVALID_EXCEPTIONAL_VALID_FROM_DATE);
                } else {
                    throw dateTimeParseException;
                }
            }
        }
    }
}
