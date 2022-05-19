package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateHelper;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AntibodyCertificateCsvBean extends CertificateGenerationCsvBean {

    @CsvBindByName(column = "sampleDate")
    private String sampleDate;
    @CsvBindByName(column = "testingCenterOrFacility")
    private String testingCenterOrFacility;

    @Override
    public AntibodyCertificateCreateDto mapToCreateDto() {
        AntibodyCertificateDataDto dataDto = new AntibodyCertificateDataDto(
                DateHelper.parse(this.sampleDate, INVALID_SAMPLE_DATE_TIME),
                testingCenterOrFacility
        );
        return super.mapToCreateDto(dataDto);
    }
}
