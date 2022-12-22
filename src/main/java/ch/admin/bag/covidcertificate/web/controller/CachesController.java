package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CacheNotFoundException;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.service.SigningInformationCacheService;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/caches")
@RequiredArgsConstructor
@Slf4j
public class CachesController {

    public final SigningClient defaultSigningClient;
    public final SigningInformationCacheService signingInformationCacheService;
    public final ValueSetsService valueSetsService;

    /**
     * Endpoint to either clear all or selected caches.
     *
     * @param only OPTIONAL list or single String seperated by "," of caches
     */
    @PostMapping("/clear")
    public void clear(@RequestParam(required = false) Optional<List<String>> only) {
        log.info("Call of clear all caches.");

        try {
            only
                    .map(cachesArray ->
                            cachesArray.stream()
                                    .flatMap(cache -> Arrays.stream(cache.split(",")))
                                    .map(String::strip)
                                    .map(String::toUpperCase)
                                    .map(Cache::valueOf)
                    )
                    .orElseGet(() -> Arrays.stream(Cache.values()))
                    .forEach(this::cleanCacheFor);

            log.info("Following caches have been reset: {}",
                    only.map(Objects::toString)
                            .orElseGet(() -> Arrays.toString(Cache.values()))
            );
        } catch (IllegalArgumentException e) {
            throw new CacheNotFoundException(e);
        }
    }


    private void cleanCacheFor(Cache cache) {
        switch (cache) {
            case KEYIDENTIFIER -> defaultSigningClient.cleanKeyIdentifierCache();
            case SIGNINGINFORMATION -> signingInformationCacheService.cleanSigningInformationCache();
            case RAPIDTESTS -> valueSetsService.cleanRapidTestsCache();
            case ISSUABLERAPIDTESTS -> valueSetsService.cleanIssuableRapidTestsCache();
            case VACCINES -> valueSetsService.cleanVaccinesCache();
            case ISSUABLEVACCINES -> valueSetsService.cleanIssuableVaccinesCache();
            case APIISSUABLEVACCINES -> valueSetsService.cleanApiIssuableVaccinesCache();
            case WEBISSUABLEVACCINES -> valueSetsService.cleanWebIssuableVaccinesCache();
            case VALUESETS -> valueSetsService.cleanValueSetsCache();
            case EXTENDEDVALUESETS -> valueSetsService.cleanExtendedValueSetsCache();
            case ISSUABLEVACCINEDTO -> valueSetsService.cleanIssuableVaccineDtoCache();
            case ISSUABLETESTDTO -> valueSetsService.cleanIssuableTestDtoCache();
            case COUNTRYCODES -> valueSetsService.cleanCountryCodesCache();
            case COUNTRYCODEBYLANGUAGE -> valueSetsService.cleanCountryCodeByLanguageCache();
        }
    }

    public enum Cache {
        KEYIDENTIFIER,
        SIGNINGINFORMATION,
        RAPIDTESTS,
        ISSUABLERAPIDTESTS,
        VACCINES,
        ISSUABLEVACCINES,
        APIISSUABLEVACCINES,
        WEBISSUABLEVACCINES,
        VALUESETS,
        EXTENDEDVALUESETS,
        ISSUABLEVACCINEDTO,
        ISSUABLETESTDTO,
        COUNTRYCODES,
        COUNTRYCODEBYLANGUAGE

    }
}
