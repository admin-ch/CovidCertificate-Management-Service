package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationTouristCertificateDataDto  implements IValidateVaccinationCertificateDataDto {

    private String medicinalProductCode;

    private Integer numberOfDoses;

    private Integer totalNumberOfDoses;

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate vaccinationDate;

    private String countryOfVaccination;
}
