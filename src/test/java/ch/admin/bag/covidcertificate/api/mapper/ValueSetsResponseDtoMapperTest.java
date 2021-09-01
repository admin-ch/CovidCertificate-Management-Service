package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueSetsResponseDtoMapperTest {
    private final JFixture jFixture = new JFixture();

    @Test
    void shouldMapCountryCodes() {
        var valueSetDto = jFixture.create(ValueSetsDto.class);
        var actual = ValueSetsResponseDtoMapper.create(valueSetDto);
        assertEquals(valueSetDto.getCountryCodes(), actual.getCountryCodes());
    }

    @Test
    void shouldMapVaccinationValueSets() {
        var valueSetDto = jFixture.create(ValueSetsDto.class);
        var actual = ValueSetsResponseDtoMapper.create(valueSetDto);
        assertEquals(valueSetDto.getVaccinationSets(), actual.getVaccinationSets());
    }

    @Test
    void shouldMapTestValueSets() {
        var valueSetDto = jFixture.create(ValueSetsDto.class);
        var actual = ValueSetsResponseDtoMapper.create(valueSetDto);
        assertEquals(valueSetDto.getTestValueSets(), actual.getTestSets());
    }
}