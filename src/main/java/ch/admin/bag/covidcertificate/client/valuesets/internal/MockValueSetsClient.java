package ch.admin.bag.covidcertificate.client.valuesets.internal;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@Profile(ProfileRegistry.VALUE_SETS_SERVICE_MOCK)
public class MockValueSetsClient implements ValueSetsClient {

    @Override
    public Map<String, ValueSetDto> getValueSets(String valueSetId) {
        log.info("Call the mock getValueSets service");
        return Map.of("1", new ValueSetDto("test", true, null));
    }
}
