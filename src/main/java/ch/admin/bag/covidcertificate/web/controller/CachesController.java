package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.service.SigningInformationCacheService;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/caches")
@RequiredArgsConstructor
@Slf4j
public class CachesController {

    public final SigningClient defaultSigningClient;
    public final SigningInformationCacheService signingInformationCacheService;
    public final ValueSetsService valueSetsService;

    /**
     *
     * Endpoint to either clear all or selected caches.
     *
     * @param only OPTIONAL list or single String seperated by "," of caches
     * @return Information about how many and which caches have been cleared
     */
    @PostMapping("/clear")
    @PreAuthorize("hasAnyRole('bag-cc-superuser')")
    public String clear(@RequestParam(required = false) Optional<List<String>> only) {
        log.info("Call of clear all caches.");

        List<Cache> caches = only
                .map(cachesArray ->
                        cachesArray.stream()
                                .flatMap(cache -> Arrays.stream(cache.split(",")))
                                .map(String::strip)
                                .map(String::toUpperCase)
                                .map(Cache::sneakyValueOf)
                                .filter(Objects::nonNull)
                )
                .orElseGet(() -> Arrays.stream(Cache.values()))
                .collect(Collectors.toList());

        caches.forEach(this::cleanCacheFor);


        String returnInfo = String.format("%d Caches are cleared: %s", caches.size(), caches);
        log.info(returnInfo);
        return returnInfo;
    }


    private void cleanCacheFor(Cache cache) {
        switch (cache) {
            case KEYIDENTIFIER:
                defaultSigningClient.cleanKeyIdentifierCache();
                break;
            case SIGNINGINFORMATION:
                signingInformationCacheService.cleanSigningInformationCache();
                break;
            case RAPIDTESTS:
                valueSetsService.cleanRapidTestsCache();
                break;
            case ISSUABLERAPIDTESTS:
                valueSetsService.cleanIssuableRapidTestsCache();
                break;
            case VACCINES:
                valueSetsService.cleanVaccinesCache();
                break;
            case ISSUABLEVACCINES:
                valueSetsService.cleanIssuableVaccinesCache();
                break;
            case APIISSUABLEVACCINES:
                valueSetsService.cleanApiIssuableVaccinesCache();
                break;
            case WEBISSUABLEVACCINES:
                valueSetsService.cleanWebIssuableVaccinesCache();
                break;
            case VALUESETS:
                valueSetsService.cleanValueSetsCache();
                break;
            case EXTENDEDVALUESETS:
                valueSetsService.cleanExtendedValueSetsCache();
                break;
            case ISSUABLEVACCINEDTO:
                valueSetsService.cleanIssuableVaccineDtoCache();
                break;
            case ISSUABLETESTDTO:
                valueSetsService.cleanIssuableTestDtoCache();
                break;
            case COUNTRYCODES:
                valueSetsService.cleanCountryCodesCache();
                break;
            case COUNTRYCODEBYLANGUAGE:
                valueSetsService.cleanCountryCodeByLanguageCache();
                break;

        }
    }

    private enum Cache {
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
        COUNTRYCODEBYLANGUAGE;


        private static Cache sneakyValueOf(String name) {
            try {
                return Cache.valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
}
