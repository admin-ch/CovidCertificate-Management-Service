package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateHelper;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryCertificateCsvBean extends CertificateCreateCsvBean {

    @CsvBindByName(column = "dateOfFirstPositiveTestResult")
    private String dateOfFirstPositiveTestResult;
    @CsvBindByName(column = "countryOfTest")
    private String countryOfTest;

    @Override
    public RecoveryCertificateCreateDto mapToCreateDto() {
        RecoveryCertificateDataDto dataDto = new RecoveryCertificateDataDto(
                DateHelper.parse(this.dateOfFirstPositiveTestResult, INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT),
                countryOfTest.trim().toUpperCase()
        );
        return super.mapToCreateDto(dataDto);
    }
}
