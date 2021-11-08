package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.CountryJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CountryCodesLoader {
    private final ObjectMapper objectMapper;
    private CountryCodes countryCodes;

    @PostConstruct
    private void loadValueSets() throws IOException {
        if (countryCodes == null) {
            countryCodes = this.loadCountryCodes();
        }
    }

    public CountryCodes getCountryCodes() {
        return this.countryCodes;
    }

    private CountryCodes loadCountryCodes() throws IOException {
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

    private InputStream getInputStream(String fileName) {
        return Objects.requireNonNull(CountryCodesLoader.class.getResourceAsStream(String.format("/valuesets/%s.json", fileName)));
    }

    private CountryJson getCountryJson(String fileName) throws IOException {
        var is = getInputStream(fileName);
        return objectMapper.readValue(is, CountryJson.class);
    }
}
