package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateDataDto implements IValidateVaccinationCertificateDataDto {

    private static final int MAX_NB_OF_DOSES = 9;

    private String medicinalProductCode;

    private Integer numberOfDoses;

    private Integer totalNumberOfDoses;

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate vaccinationDate;

    private String countryOfVaccination;
}
