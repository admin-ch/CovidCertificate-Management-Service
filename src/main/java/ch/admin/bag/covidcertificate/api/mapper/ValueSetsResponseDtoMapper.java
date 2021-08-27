package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.response.ValueSetsResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValueSetsResponseDtoMapper {
        public static ValueSetsResponseDto create(ValueSetsDto valueSetsDto){
        return new ValueSetsResponseDto(
                valueSetsDto.getCountryCodes(),
                valueSetsDto.getVaccinationSets(),
                valueSetsDto.getTestValueSets()
        );
    }
}
