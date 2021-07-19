package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.valueset.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
                    getChAcceptedTestValueSet(),
                    getAllTestValueSet()
            );
        }
    }

    public ValueSetsDto getValueSets() {
        return valueSetsDto;
    }

    private CountryCodes getCountryCodes() throws IOException {
        var deJson = getCountryJson("country-alpha-2-de");
        var frJson = getCountryJson("country-alpha-2-fr");
        var itJson = getCountryJson("country-alpha-2-it");
        var enJson = getCountryJson("country-alpha-2-en");
        var rmJson = getCountryJson("country-alpha-2-rm");
        return new CountryCodes(
                deJson.getValueSetValues(),
                enJson.getValueSetValues(),
                frJson.getValueSetValues(),
                itJson.getValueSetValues(),
                rmJson.getValueSetValues()
        );
    }

    private List<VaccinationValueSet> getVaccinationValueSet() throws IOException {
        return getVaccinationJson().getEntries();
    }

    private List<TestValueSet> getAllTestValueSet() throws IOException {
        return new ArrayList<>(getTestSetJson()
                .getEntries());
    }


    private List<TestValueSet> getChAcceptedTestValueSet() throws IOException {
        return getAllTestValueSet()
                .stream()
                .filter(TestValueSet::isChAccepted)
                .collect(Collectors.toList());
    }

    private InputStream getInputStream(String fileName) {
        return Objects.requireNonNull(ValueSetsLoader.class.getResourceAsStream(String.format("/valuesets/%s.json", fileName)));
    }

    private CountryJson getCountryJson(String fileName) throws IOException {
        var is = getInputStream(fileName);
        return objectMapper.readValue(is, CountryJson.class);
    }

    private VaccinationSetJson getVaccinationJson() throws IOException {
        var is = getInputStream("covid-19-vaccines");
        return objectMapper.readValue(is, VaccinationSetJson.class);
    }

    private TestSetJson getTestSetJson() throws IOException {
        var is = getInputStream("covid-19-tests");
        return objectMapper.readValue(is, TestSetJson.class);
    }
}
