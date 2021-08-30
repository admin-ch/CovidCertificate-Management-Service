package ch.admin.bag.covidcertificate.client.valuesets.internal;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetsResponseDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!" + ProfileRegistry.VALUE_SETS_SERVICE_MOCK)
public class DefaultValueSetsClient implements ValueSetsClient {

    @Value("${ch-covidcertificate-backend-verifier-service.url}")
    private String serviceUri;

    private final WebClient defaultWebClient;

    @Override
    public Map<String, ValueSetDto> getValueSets(String valueSetId)  {
        var builder = UriComponentsBuilder.fromHttpUrl(serviceUri).queryParam("valueSetId", valueSetId);

        var uri = builder.build().toString();
        log.debug("Call the ch-covidcertificate-backend-verifier-service with url {}", uri);
        try {
            var response = defaultWebClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ValueSetsResponseDto.class)
                    .block();
            if (response != null) {
                log.trace("ch-covidcertificate-backend-verifier-service response: {}", response);
                log.debug("Received {} entries", response.getValueSetValues().size());
                return response.getValueSetValues();
            } else {
                log.error("Response from {} is null", uri);
                throw new IllegalStateException("Response is null");
            }
        } catch (Exception e) {
            log.error("Request to {} failed", uri, e);
            throw new IllegalStateException(e);
        }
    }
}
