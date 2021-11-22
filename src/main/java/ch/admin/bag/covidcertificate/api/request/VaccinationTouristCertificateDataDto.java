package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationTouristCertificateDataDto {

    private static final int MAX_NB_OF_DOSES = 9;

    private String medicinalProductCode;

    private Integer numberOfDoses;

    private Integer totalNumberOfDoses;

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate vaccinationDate;

    private String countryOfVaccination;

    public void validate() {
        if (numberOfDoses == null ||
                numberOfDoses < 1 ||
                totalNumberOfDoses == null ||
                numberOfDoses > totalNumberOfDoses ||
                numberOfDoses > MAX_NB_OF_DOSES ||
                totalNumberOfDoses > MAX_NB_OF_DOSES) {
            throw new CreateCertificateException(INVALID_DOSES);
        }
        if (vaccinationDate == null || vaccinationDate.isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_VACCINATION_DATE);
        }
        if (countryOfVaccination == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
    }
}
