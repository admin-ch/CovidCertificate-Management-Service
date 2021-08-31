package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        valueSets.put("1", new ValueSetDto("test1", true));
        valueSets.put("2", new ValueSetDto("test2", true));
        when(valueSetsClient.getValueSets(anyString())).thenReturn(valueSets);

        RapidTest rapidTestMock = mock(RapidTest.class);
        when(repository.findAllByActiveAndModifiedAtIsNot(anyBoolean(), any(LocalDateTime.class))).thenReturn(List.of(rapidTestMock));

        service.importRapidTests();
        verify(repository, times(2)).saveAndFlush(any(RapidTest.class));
        verify(rapidTestMock).deactivate(any(LocalDateTime.class));
    }
}
