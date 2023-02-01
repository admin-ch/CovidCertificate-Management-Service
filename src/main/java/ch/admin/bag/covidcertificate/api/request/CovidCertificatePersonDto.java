package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_BIRTH_IN_FUTURE;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.MIN_DATE_OF_BIRTH;
import static ch.admin.bag.covidcertificate.api.Constants.NO_PERSON_DATA;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonDto {

    @NotNull(message = "No person data was specified")
    @Valid
    private CovidCertificatePersonNameDto name;

    @NotNull(message = "Date of birth must not be null")
    private String dateOfBirth;

    @AssertTrue(message = "Invalid dateOfBirth! Must be younger than 1900-01-01")
    public boolean isValidDateOfBirth() {
        LocalDate parsedDateOfBirth;
        if (Objects.isNull(dateOfBirth)) return true;

        try {
            parsedDateOfBirth = DateHelper.parseDateOfBirth(dateOfBirth);
        } catch(CreateCertificateException e) {
            return false;
        }
        return !parsedDateOfBirth.isBefore(MIN_DATE_OF_BIRTH) && !parsedDateOfBirth.isAfter(MAX_DATE_OF_BIRTH);
    }

    @AssertTrue(message = "Invalid dateOfBirth! Date cannot be in the future")
    public boolean isValidDateOfBirthInFuture() {
        LocalDate parsedDateOfBirth;
        if (Objects.isNull(dateOfBirth)) return true;
        try {
            parsedDateOfBirth = DateHelper.parseDateOfBirth(dateOfBirth);
        } catch(CreateCertificateException e) {
            return false;
        }
        return !parsedDateOfBirth.isAfter(LocalDate.now());
    }
}
