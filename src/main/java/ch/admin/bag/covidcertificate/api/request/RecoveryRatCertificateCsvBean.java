package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryRatCertificateCsvBean extends CertificateCreateCsvBean {

    public static final String TIME = "T";

    @CsvBindByName(column = "sampleDateTime")
    private String sampleDateTime;
    @CsvBindByName(column = "memberStateOfTest")
    private String memberStateOfTest;

    @Override
    public RecoveryRatCertificateCreateDto mapToCreateDto() {
        ZonedDateTime sampleDateTimeParsed;
        try {
            if (this.sampleDateTime.contains(TIME)) {
                // it contains a time
                if (this.sampleDateTime.contains("Z")) {
                    // it is UTC zoned
                    ZonedDateTime utcZoned = ZonedDateTime.parse(this.sampleDateTime);
                    sampleDateTimeParsed = utcZoned.withZoneSameInstant(SWISS_TIMEZONE);
                } else {
                    // it is un zoned and we interpret it as SWISS_TIMEZONE
                    sampleDateTimeParsed = LocalDateTime.parse(this.sampleDateTime).atZone(SWISS_TIMEZONE);
                }
            } else {
                // it is without time and we take start of day with SWISS_TIMEZONE
                sampleDateTimeParsed = LocalDate.parse(sampleDateTime).atStartOfDay(SWISS_TIMEZONE);
            }
        } catch (Exception e) {
            throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
        }

        var dataDto = new RecoveryRatCertificateDataDto(
                sampleDateTimeParsed,
              "",
                (memberStateOfTest != null) ? memberStateOfTest.trim().toUpperCase(): ""
        );
        return super.mapToCreateDto(dataDto);
    }
}
