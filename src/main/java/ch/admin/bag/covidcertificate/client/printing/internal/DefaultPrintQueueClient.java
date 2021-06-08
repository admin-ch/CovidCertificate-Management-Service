package ch.admin.bag.covidcertificate.client.printing.internal;

import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!" + ProfileRegistry.PRINTING_SERVICE_MOCK)
public class DefaultPrintQueueClient implements PrintQueueClient {

    @Value("${cc-printing-service.url}")
    private String serviceUri;

    private final WebClient defaultWebClient;

    @Override
    public boolean sendPrintJob(CertificatePrintRequestDto printRequestDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serviceUri);

        String uri = builder.toUriString();
        log.debug("Call the PrintingService with url {}", serviceUri);
        try {
            ResponseEntity<Void> response = defaultWebClient.post()
                    .uri(uri)
                    .body(Mono.just(printRequestDto), printRequestDto.getClass())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.trace("PrintingService Response: {}", response);
            return response != null && response.getStatusCodeValue() == 201;

        } catch (WebClientResponseException e) {
            log.warn("Received error message: {}", e.getResponseBodyAsString());
        }

        return false;
    }
}
