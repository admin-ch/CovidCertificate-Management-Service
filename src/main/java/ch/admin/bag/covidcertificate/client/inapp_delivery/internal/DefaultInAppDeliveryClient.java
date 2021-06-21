package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.Constants.USER_EXT_ID_CLAIM_KEY;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!" + ProfileRegistry.INAPP_DELIVERY_SERVICE_MOCK)
public class DefaultInAppDeliveryClient implements InAppDeliveryClient {

    @Value("${cc-inapp-delivery-service.url}")
    private String serviceUri;

    private final WebClient defaultWebClient;
    private final ServletJeapAuthorization jeapAuthorization;
    private final KpiDataService kpiLogService;

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
            logKpi();
        } catch (WebClientResponseException e) {
            log.error("Received error message", e);
            throw new CreateCertificateException(INAPP_DELIVERY_FAILED);
        } catch (WebClientRequestException e) {
            log.error("Request to {} failed", serviceUri, e);
            throw new CreateCertificateException(INAPP_DELIVERY_FAILED);
        }
    }

    private void logKpi() {
        String extId = jeapAuthorization.getExtIdInAuthentication();
        if (extId != null) {
            LocalDateTime kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {}", kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)), kv(KPI_TYPE_KEY, KPI_TYPE_INAPP_DELIVERY), kv(KPI_UUID_KEY, extId));
            kpiLogService.log(new KpiData(kpiTimestamp, KPI_TYPE_INAPP_DELIVERY, extId));
        }
    }
}
