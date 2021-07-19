package ch.admin.bag.covidcertificate.api.valueset;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValueSetsDto {
    private final CountryCodes countryCodes;
    private final List<VaccinationValueSet> vaccinationSets;
    private final List<TestValueSet> chAcceptedTestValueSets;
    private final List<TestValueSet> allTestValueSets;
}
