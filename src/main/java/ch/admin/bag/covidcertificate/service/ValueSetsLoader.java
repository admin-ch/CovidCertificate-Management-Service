package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.valueset.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValueSetsLoader {
    private final ObjectMapper objectMapper;
    private ValueSetsDto valueSetsDto;

    @PostConstruct
    private void loadValueSets() throws IOException {
        if (valueSetsDto == null) {
            valueSetsDto = new ValueSetsDto(
                    getCountryCodes(),
                    getVaccinationValueSet(),
                    getChAcceptedTestValueSet()
            );
        }
    }

    public ValueSetsDto getValueSets() {
        return valueSetsDto;
    }

    private CountryCodes getCountryCodes() throws IOException {
        CountryJson deJson = getCountryJson("country-alpha-2-de");
        CountryJson frJson = getCountryJson("country-alpha-2-fr");
        CountryJson itJson = getCountryJson("country-alpha-2-it");
        CountryJson enJson = getCountryJson("country-alpha-2-en");
        return new CountryCodes(
                deJson.getValueSetValues(),
                enJson.getValueSetValues(),
                frJson.getValueSetValues(),
                itJson.getValueSetValues()
        );
    }

    private List<VaccinationValueSet> getVaccinationValueSet() throws IOException {
        return getVaccinationJson().getEntries();
    }

    private List<TestValueSet> getChAcceptedTestValueSet() throws IOException {
        return getTestSetJson()
                .getEntries()
                .stream()
                .filter(TestValueSet::isChAccepted)
                .collect(Collectors.toList());
    }

    private InputStream getInputStream(String fileName) {
        return Objects.requireNonNull(ValueSetsLoader.class.getResourceAsStream(String.format("/valuesets/%s.json", fileName)));
    }

    private CountryJson getCountryJson(String fileName) throws IOException {
        InputStream is = getInputStream(fileName);
        return objectMapper.readValue(is, CountryJson.class);
    }

    private VaccinationSetJson getVaccinationJson() throws IOException {
        InputStream is = getInputStream("covid-19-vaccines");
        return objectMapper.readValue(is, VaccinationSetJson.class);
    }

    private TestSetJson getTestSetJson() throws IOException {
        InputStream is = getInputStream("covid-19-tests");
        return objectMapper.readValue(is, TestSetJson.class);
    }
}
