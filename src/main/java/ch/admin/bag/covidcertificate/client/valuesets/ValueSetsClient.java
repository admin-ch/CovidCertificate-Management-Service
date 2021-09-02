package ch.admin.bag.covidcertificate.client.valuesets;

import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;

import java.util.Map;

public interface ValueSetsClient {

    Map<String, ValueSetDto> getValueSets(String valueSetId);
}
