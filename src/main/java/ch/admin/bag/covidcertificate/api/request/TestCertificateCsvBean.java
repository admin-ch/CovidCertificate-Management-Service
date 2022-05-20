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
public class TestCertificateCsvBean extends CertificateCreateCsvBean {

    @CsvBindByName(column = "manufacturerCode")
    private String manufacturerCode;
    @CsvBindByName(column = "typeCode")
    private String typeCode;
    @CsvBindByName(column = "sampleDateTime")
    private String sampleDateTime;
    @CsvBindByName(column = "testingCentreOrFacility")
    private String testingCentreOrFacility;
    @CsvBindByName(column = "memberStateOfTest")
    private String memberStateOfTest;

    @Override
    public TestCertificateCreateDto mapToCreateDto() {
        ZonedDateTime sampleDateTimeParsed;
        try {
            sampleDateTimeParsed = ZonedDateTime.parse(this.sampleDateTime);
        } catch (Exception e) {
            throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
        }

        var dataDto = new TestCertificateDataDto(
                (manufacturerCode != null) ? manufacturerCode.trim(): "",
                (typeCode != null) ? typeCode.trim(): "",
                sampleDateTimeParsed,
                (testingCentreOrFacility != null) ? testingCentreOrFacility.trim(): "",
                (memberStateOfTest != null) ? memberStateOfTest.trim().toUpperCase(): ""
        );
        return super.mapToCreateDto(dataDto);
    }
}
