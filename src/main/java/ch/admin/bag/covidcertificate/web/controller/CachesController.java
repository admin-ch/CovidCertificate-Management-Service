package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.service.SigningInformationCacheService;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/caches")
@RequiredArgsConstructor
@Slf4j
public class CachesController {

    public final SigningClient defaultSigningClient;
    public final SigningInformationCacheService signingInformationCacheService;
    public final ValueSetsService valueSetsService;

    @PostMapping("/clear")
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public void clear() {
        log.info("Call of clear all caches.");
        defaultSigningClient.cleanKeyIdentifierCache();
        signingInformationCacheService.cleanSigningInformationCache();
        valueSetsService.cleanRapidTestsCache();
        valueSetsService.cleanIssuableRapidTestsCache();
        valueSetsService.cleanVaccinesCache();
        valueSetsService.cleanIssuableVaccinesCache();
        valueSetsService.cleanApiIssuableVaccinesCache();
        valueSetsService.cleanWebIssuableVaccinesCache();
        valueSetsService.cleanValueSetsCache();
        valueSetsService.cleanExtendedValueSetsCache();
        valueSetsService.cleanIssuableVaccineDtoCache();
        valueSetsService.cleanIssuableTestDtoCache();
        valueSetsService.cleanCountryCodesCache();
        valueSetsService.cleanCountryCodeByLanguageCache();
    }
}
