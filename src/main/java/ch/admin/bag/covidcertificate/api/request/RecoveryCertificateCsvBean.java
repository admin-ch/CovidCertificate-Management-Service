package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryCertificateCsvBean extends CertificateCsvBean {

    @CsvBindByName(column = "dateOfFirstPositiveTestResult")
    private String dateOfFirstPositiveTestResult;
    @CsvBindByName(column = "countryOfTest")
    private String countryOfTest;

    @Override
    public RecoveryCertificateCreateDto mapToCreateDto() {
        LocalDate dateOfFirstPositiveTestResult;
        try {
            dateOfFirstPositiveTestResult = LocalDate.parse(this.dateOfFirstPositiveTestResult);
        } catch (Exception e) {
            throw new CreateCertificateException(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT);
        }
        RecoveryCertificateDataDto dataDto = new RecoveryCertificateDataDto(
                dateOfFirstPositiveTestResult,
                countryOfTest
        );
        return mapToCreateDto(dataDto);
    }
}
