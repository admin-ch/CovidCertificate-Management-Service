package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValueSetsResponseDto {
    private final CountryCodes countryCodes;
    private final List<VaccinationValueSet> vaccinationSets;
    private final List<TestValueSet> testSets;
}
