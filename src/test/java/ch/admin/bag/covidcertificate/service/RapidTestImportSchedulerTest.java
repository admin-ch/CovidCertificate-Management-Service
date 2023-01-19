package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RapidTestImportSchedulerTest {

    @InjectMocks
    private RapidTestImportScheduler service;

    @Mock
    private ValueSetsClient valueSetsClient;

    @Mock
    private RapidTestRepository repository;

    @Test
    void importRapidTests() {

        Map<String, ValueSetDto> valueSets = new HashMap<>();
        valueSets.put("1", new ValueSetDto("test1", true, null));
        valueSets.put("2", new ValueSetDto("test2", true, null));
        when(valueSetsClient.getValueSets(anyString())).thenReturn(valueSets);

        RapidTest rapidTestMock = mock(RapidTest.class);
        when(repository.findAllByActiveAndModifiedAtIsNot(anyBoolean(), any(LocalDateTime.class))).thenReturn(List.of(rapidTestMock));

        service.importRapidTests();
        verify(repository, times(2)).saveAndFlush(any(RapidTest.class));
        verify(rapidTestMock).deactivate(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Given the value-sets (management-service) are sync every-day at 03:00 AM from the eu-value-sets (backend-verifier-service), when the eu-value-sets are returned with the property 'validUntil', then it should persist the new property in the 'covid_19_lab_test_manufacturer_and_name' table from 'cc-management' data-base.")
    void importRapidTests2() {

        Map<String, ValueSetDto> valueSets = new HashMap<>();
        valueSets.put("1", new ValueSetDto("test1", true, ZonedDateTime.parse("2021-12-08 00:00:00 CET", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))));
        valueSets.put("2", new ValueSetDto("test2", true, ZonedDateTime.parse("2021-12-08 00:00:00 CET", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"))));
        when(valueSetsClient.getValueSets(anyString())).thenReturn(valueSets);

        RapidTest rapidTestMock = mock(RapidTest.class);
        when(repository.findAllByActiveAndModifiedAtIsNot(anyBoolean(), any(LocalDateTime.class))).thenReturn(List.of(rapidTestMock));

        service.importRapidTests();
        verify(repository, times(2)).saveAndFlush(any(RapidTest.class));
        verify(rapidTestMock).deactivate(any(LocalDateTime.class));
    }
}
