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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/valuesets")
@RequiredArgsConstructor
@Slf4j
public class ValueSetsController {
    private final SecurityHelper securityHelper;
    private final ValueSetsService valueSetsService;

    @GetMapping("")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public ValueSetsResponseDto get(HttpServletRequest request) {
        log.info("Call to get value sets.");
        securityHelper.authorizeUser(request);

        return ValueSetsResponseDtoMapper.create(valueSetsService.getValueSets());
    }

    @GetMapping("/rapid-tests")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<TestDto> getRapidTests(HttpServletRequest request) {
        log.info("Call of getRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getRapidTests();
    }

    @GetMapping("/issuable-rapid-tests")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableTestDto> getIssuableRapidTests(HttpServletRequest request) {
        log.info("Call of getIssuableRapidTests for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getIssuableRapidTests();
    }

    @GetMapping("/vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<VaccineDto> getVaccines(HttpServletRequest request) {
        log.info("Call of getVaccines for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getVaccines();
    }

    @GetMapping("/issuable-vaccines")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableVaccineDto> getIssuableVaccines(HttpServletRequest request) {
        securityHelper.authorizeUser(request);

        log.info("Call of getIssuableVaccines for value sets with systemSource {}", SystemSource.ApiGateway);
        return valueSetsService.getApiGatewayIssuableVaccines();
    }

    @GetMapping("/issuable-vaccines/{systemSource}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<IssuableVaccineDto> getIssuableVaccines(@PathVariable String systemSource, HttpServletRequest request) {
        securityHelper.authorizeUser(request);

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
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public CountryCodes getCountryCodes(HttpServletRequest request) {
        log.info("Call of getCountryCodes for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getCountryCodes();
    }

    @GetMapping("/countries/{language}")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public List<CountryCode> getCountryCodesByLanguage(@PathVariable final String language, HttpServletRequest request) {
        log.info("Call of getCountryCodes by language for value sets");
        securityHelper.authorizeUser(request);

        return valueSetsService.getCountryCodesForLanguage(language);
    }
}
