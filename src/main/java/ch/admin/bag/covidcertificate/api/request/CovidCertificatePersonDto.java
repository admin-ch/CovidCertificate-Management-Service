package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateHelper;
import lombok.*;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonDto {

    private CovidCertificatePersonNameDto name;

    private String dateOfBirth;

    public void validate() {
        if (name == null) {
            throw new CreateCertificateException(NO_PERSON_DATA);
        } else {
            name.validate();
        }
        validateDateOfBirth(dateOfBirth);
    }

    private void validateDateOfBirth(String dateOfBirth) {
        var parsedDateOfBirth = DateHelper.parseDateOfBirth(dateOfBirth);
        if (parsedDateOfBirth.isBefore(MIN_DATE_OF_BIRTH) || parsedDateOfBirth.isAfter(MAX_DATE_OF_BIRTH)) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
        if (parsedDateOfBirth.isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH_IN_FUTURE);
        }
    }
}
