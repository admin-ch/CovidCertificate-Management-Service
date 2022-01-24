package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.service.SigningInformationCacheService;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
import ch.admin.bag.covidcertificate.web.controller.CachesController.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CachesControllerTest {

    @InjectMocks
    private CachesController cachesController;
    @Mock
    private SigningClient defaultSigningClient;
    @Mock
    private SigningInformationCacheService signingInformationCacheService;
    @Mock
    private ValueSetsService valueSetsService;

    @Test
    public void shouldClearAllCaches() {
        cachesController.clear(Optional.empty());
        verify(defaultSigningClient, times(1)).cleanKeyIdentifierCache();
        verify(signingInformationCacheService, times(1)).cleanSigningInformationCache();
        verify(valueSetsService, times(1)).cleanRapidTestsCache();
        verify(valueSetsService, times(1)).cleanIssuableRapidTestsCache();
        verify(valueSetsService, times(1)).cleanVaccinesCache();
        verify(valueSetsService, times(1)).cleanIssuableVaccinesCache();
        verify(valueSetsService, times(1)).cleanApiIssuableVaccinesCache();
        verify(valueSetsService, times(1)).cleanWebIssuableVaccinesCache();
        verify(valueSetsService, times(1)).cleanValueSetsCache();
        verify(valueSetsService, times(1)).cleanExtendedValueSetsCache();
        verify(valueSetsService, times(1)).cleanIssuableVaccineDtoCache();
        verify(valueSetsService, times(1)).cleanIssuableTestDtoCache();
        verify(valueSetsService, times(1)).cleanCountryCodesCache();
        verify(valueSetsService, times(1)).cleanCountryCodeByLanguageCache();
    }


    @Test
    public void shouldClearCachesFromList() {
        var caches = List.of(Cache.KEYIDENTIFIER.name(), Cache.SIGNINGINFORMATION.name());
        cachesController.clear(Optional.of(caches));
        verify(defaultSigningClient, times(1)).cleanKeyIdentifierCache();
        verify(signingInformationCacheService, times(1)).cleanSigningInformationCache();
        verify(valueSetsService, times(0)).cleanRapidTestsCache();
        verify(valueSetsService, times(0)).cleanIssuableRapidTestsCache();
        verify(valueSetsService, times(0)).cleanVaccinesCache();
        verify(valueSetsService, times(0)).cleanIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanApiIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanWebIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanValueSetsCache();
        verify(valueSetsService, times(0)).cleanExtendedValueSetsCache();
        verify(valueSetsService, times(0)).cleanIssuableVaccineDtoCache();
        verify(valueSetsService, times(0)).cleanIssuableTestDtoCache();
        verify(valueSetsService, times(0)).cleanCountryCodesCache();
        verify(valueSetsService, times(0)).cleanCountryCodeByLanguageCache();
    }

    @Test
    public void shouldClearCachesFromString() {
        var caches = Stream.of(Cache.COUNTRYCODES, Cache.COUNTRYCODEBYLANGUAGE)
                .map(Enum::name)
                .collect(Collectors.joining(", "));
        cachesController.clear(Optional.of(List.of(caches)));
        verify(defaultSigningClient, times(0)).cleanKeyIdentifierCache();
        verify(signingInformationCacheService, times(0)).cleanSigningInformationCache();
        verify(valueSetsService, times(0)).cleanRapidTestsCache();
        verify(valueSetsService, times(0)).cleanIssuableRapidTestsCache();
        verify(valueSetsService, times(0)).cleanVaccinesCache();
        verify(valueSetsService, times(0)).cleanIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanApiIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanWebIssuableVaccinesCache();
        verify(valueSetsService, times(0)).cleanValueSetsCache();
        verify(valueSetsService, times(0)).cleanExtendedValueSetsCache();
        verify(valueSetsService, times(0)).cleanIssuableVaccineDtoCache();
        verify(valueSetsService, times(0)).cleanIssuableTestDtoCache();
        verify(valueSetsService, times(1)).cleanCountryCodesCache();
        verify(valueSetsService, times(1)).cleanCountryCodeByLanguageCache();
    }


}
