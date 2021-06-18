package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static ch.admin.bag.covidcertificate.api.Constants.INAPP_DELIVERY_FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!" + ProfileRegistry.INAPP_DELIVERY_SERVICE_MOCK)
public class DefaultInAppDeliveryClient implements InAppDeliveryClient {

    @Value("${cc-inapp-delivery-service.url}")
    private String serviceUri;

    private final WebClient defaultWebClient;

    @Override
    public void deliverToApp(InAppDeliveryRequestDto requestDto) {
        var builder = UriComponentsBuilder.fromHttpUrl(serviceUri);

        var uri = builder.toUriString();
        log.debug("Call the InApp Delivery Backend with url {}", serviceUri);
        try {
            var response = defaultWebClient.post()
                    .uri(uri)
                    .body(Mono.just(requestDto), requestDto.getClass())
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.trace("InApp Delivery Backend Response: {}", response);
            if (response == null || response.getStatusCodeValue() != 200) {
                throw new CreateCertificateException(INAPP_DELIVERY_FAILED);
            }
        } catch (WebClientResponseException e) {
            log.error("Received error message", e);
            throw new CreateCertificateException(INAPP_DELIVERY_FAILED);
        } catch (WebClientRequestException e) {
            log.error("Request to {} failed", serviceUri, e);
            throw new CreateCertificateException(INAPP_DELIVERY_FAILED);
        }
    }
}
