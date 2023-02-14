package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryCertificateDataDto {

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate dateOfFirstPositiveTestResult;

    @NotNull(message = "Invalid country of test")
    private String countryOfTest;

    @AssertFalse(message = "Invalid date of first positive test result")
    public boolean isValidDteOfFirstPositiveTestResult() {
        return dateOfFirstPositiveTestResult == null || dateOfFirstPositiveTestResult.isAfter(LocalDate.now());
    }
}
