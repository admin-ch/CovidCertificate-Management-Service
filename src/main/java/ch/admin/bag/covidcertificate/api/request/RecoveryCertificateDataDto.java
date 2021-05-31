package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryCertificateDataDto {

    private LocalDate dateOfFirstPositiveTestResult;

    private String countryOfTest;

    public void validate() {
        if (dateOfFirstPositiveTestResult == null || dateOfFirstPositiveTestResult.isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT);
        }
        if (countryOfTest == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }
    }
}
