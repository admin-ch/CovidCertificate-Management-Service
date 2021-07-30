package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateHelper;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_OR_RESULT_DATE_TIME;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TestCertificateCsvBean extends CertificateCsvBean {

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
        TestCertificateDataDto dataDto = new TestCertificateDataDto(
                manufacturerCode.trim(),
                typeCode.trim(),
                DateHelper.parseZonedDate(this.sampleDateTime, INVALID_SAMPLE_OR_RESULT_DATE_TIME),
                testingCentreOrFacility.trim(),
                memberStateOfTest.trim().toUpperCase()
        );
        return super.mapToCreateDto(dataDto);
    }
}
