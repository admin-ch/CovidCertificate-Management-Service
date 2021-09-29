package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateDataDto {

    private String medicinalProductCode;

    private Integer numberOfDoses;

    private Integer totalNumberOfDoses;

    private LocalDate vaccinationDate;

    private String countryOfVaccination;

    private SystemSource systemSource;

    public void validate() {
        if (systemSource == null) {
            throw new IllegalStateException("mandatory attribute systemSource is missing. Check Request implementation.");
        }
        if (numberOfDoses == null ||
                numberOfDoses < 1 ||
                totalNumberOfDoses == null ||
                numberOfDoses > totalNumberOfDoses) {
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
