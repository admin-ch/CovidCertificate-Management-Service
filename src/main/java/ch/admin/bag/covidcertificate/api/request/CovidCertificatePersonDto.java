package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonDto {

    private CovidCertificatePersonNameDto name;

    private LocalDate dateOfBirth;

    public void validate() {
        if (name == null) {
            throw new CreateCertificateException(NO_PERSON_DATA);
        } else {
            name.validate();
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now()) || dateOfBirth.isBefore(MIN_DATE_OF_BIRTH) || dateOfBirth.isAfter(MAX_DATE_OF_BIRTH)) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
    }
}
