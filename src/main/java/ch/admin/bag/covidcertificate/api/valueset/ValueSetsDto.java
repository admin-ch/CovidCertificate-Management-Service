package ch.admin.bag.covidcertificate.api.valueset;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValueSetsDto {
    private final CountryCodes countryCodes;
    private final List<IssuableVaccineDto> vaccinationSets;
    private final List<IssuableTestDto> testValueSets;
}
