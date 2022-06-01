package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.mapper.ValueSetsResponseDtoMapper;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.response.ValueSetsResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/valuesets")
@RequiredArgsConstructor
@Slf4j
public class ValueSetsController {
    private final ValueSetsService valueSetsService;

    @GetMapping("")
    public ValueSetsResponseDto get() {
        log.info("Call to get value sets.");
        return ValueSetsResponseDtoMapper.create(valueSetsService.getValueSets());
    }

    @GetMapping("/rapid-tests")
    public List<TestDto> getRapidTests() {
        log.info("Call of getRapidTests for value sets");
        return valueSetsService.getRapidTests();
    }

    @GetMapping("/issuable-rapid-tests")
    public List<IssuableTestDto> getIssuableRapidTests() {
        log.info("Call of getIssuableRapidTests for value sets");
        return valueSetsService.getIssuableRapidTests();
    }

    @GetMapping("/vaccines")
    public List<VaccineDto> getVaccines() {
        log.info("Call of getVaccines for value sets");
        return valueSetsService.getVaccines();
    }

    @GetMapping("/issuable-vaccines")
    public List<IssuableVaccineDto> getIssuableVaccines() {
        log.info("Call of getIssuableVaccines for value sets with systemSource {}", SystemSource.ApiGateway);
        return valueSetsService.getApiGatewayIssuableVaccines();
    }

    @GetMapping("/issuable-vaccines/{systemSource}")
    public List<IssuableVaccineDto> getIssuableVaccines(@PathVariable String systemSource) {
        SystemSource localSystemSource = SystemSource.valueOf(systemSource);
        log.info("Call of getIssuableVaccines for value sets with systemSource {}", localSystemSource);
        switch (localSystemSource) {
            case WebUI:
                return valueSetsService.getWebUiIssuableVaccines();
            case CsvUpload:
            case ApiGateway:
                return valueSetsService.getApiGatewayIssuableVaccines();
            case ApiPlatform:
                return valueSetsService.getApiPlatformIssuableVaccines();
        }
        return valueSetsService.getApiGatewayIssuableVaccines();
    }

    @GetMapping("/countries")
    public CountryCodes getCountryCodes() {
        log.info("Call of getCountryCodes for value sets");
        return valueSetsService.getCountryCodes();
    }

    @GetMapping("/countries/{language}")
    public List<CountryCode> getCountryCodesByLanguage(@PathVariable final String language) {
        log.info("Call of getCountryCodes by language for value sets");
        return valueSetsService.getCountryCodesForLanguage(language);
    }
}
