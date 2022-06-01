package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryRatCertificateCsvBean extends CertificateCreateCsvBean {

    @CsvBindByName(column = "sampleDateTime")
    private String sampleDateTime;
    @CsvBindByName(column = "memberStateOfTest")
    private String memberStateOfTest;

    @Override
    public RecoveryRatCertificateCreateDto mapToCreateDto() {
        ZonedDateTime sampleDateTimeParsed;
        try {
            sampleDateTimeParsed = ZonedDateTime.parse(this.sampleDateTime);
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
