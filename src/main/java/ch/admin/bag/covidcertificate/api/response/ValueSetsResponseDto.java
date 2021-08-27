package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValueSetsResponseDto {
    private final CountryCodes countryCodes;
    private final List<IssuableVaccineDto> vaccinationSets;
    private final List<IssuableTestDto> testSets;
}
