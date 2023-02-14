package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.apache.commons.lang3.Range;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateDataDto {

    private String medicinalProductCode;

    private Integer numberOfDoses;

    private Integer totalNumberOfDoses;

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate vaccinationDate;

    private String countryOfVaccination;

    @AssertTrue(message = "Invalid medicinal product")
    public boolean isMedicinalProductCodeValid() {
        return getMedicinalProductCode() != null && !getMedicinalProductCode().isEmpty();
    }

    @AssertTrue(message = "Invalid number of doses")
    public boolean isDosesValid() {
        Range<Integer> validDoseValueRange = Range.between(Constants.MIN_NB_OF_DOSES, Constants.MAX_NB_OF_DOSES);

        return validDoseValueRange.contains(getNumberOfDoses()) &&
                validDoseValueRange.contains(getTotalNumberOfDoses()) &&
                (getNumberOfDoses() <= getTotalNumberOfDoses() || getTotalNumberOfDoses() == 1);
    }

    @AssertTrue(message = "Invalid vaccination date! Date cannot be in the future")
    public boolean isVaccinationDateValid() {
        return getVaccinationDate() != null && !getVaccinationDate().isAfter(LocalDate.now());
    }

    @AssertTrue(message = "Invalid country of vaccination")
    public boolean isCountryOfVaccinationValid() {
        return getCountryOfVaccination() != null && !getCountryOfVaccination().isEmpty();
    }
}