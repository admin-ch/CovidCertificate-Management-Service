package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.util.UserExtIdHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.APP_DELIVERY_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_IN_APP_DELIVERY_CODE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_IN_APP_DELIVERY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UVCI_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.UNKNOWN_APP_CODE;
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
    public CreateCertificateError deliverToApp(
            String uvci, SystemSource systemSource, String userExtId, InAppDeliveryRequestDto requestDto) {

        final var uri = UriComponentsBuilder.fromHttpUrl(serviceUri).toUriString();
        log.debug("Call the InApp Delivery Backend with url {}", uri);
        try {
            var response = defaultWebClient.post()
                    .uri(uri)
                    .body(Mono.just(requestDto), requestDto.getClass())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            log.trace("InApp Delivery Backend Response: {}", response);
            if (response != null && response.getStatusCode().value() == 200) {
                final String code = requestDto.getCode();
                logKpi(uvci, systemSource, userExtId, code);
                return null;
            } else {
                throw new CreateCertificateException(APP_DELIVERY_FAILED);
            }
        } catch (WebClientResponseException e) {
            return this.handleErrorResponse(requestDto, uri, e);
        } catch (WebClientRequestException e) {
            log.error("Call the InApp Delivery Backend for transfer code '{}' with url '{}' failed", requestDto.getCode(), uri, e);
            return APP_DELIVERY_FAILED;
        }
    }

    private CreateCertificateError handleErrorResponse(InAppDeliveryRequestDto requestDto, String uri, WebClientResponseException exception) {
        if (exception != null && exception.getStatusCode() == HttpStatus.I_AM_A_TEAPOT) {
            log.warn("InApp Delivery Backend returned '{}' for transfer code '{}'", HttpStatus.I_AM_A_TEAPOT.value(), requestDto.getCode());
            return UNKNOWN_APP_CODE;
        } else {
            log.error("Call the InApp Delivery Backend for transfer code '{}' with url '{}' failed", requestDto.getCode(), uri, exception);
            return APP_DELIVERY_FAILED;
        }
    }

    private void logKpi(String uvci, SystemSource systemSource, String userExtId, String inAppDeliveryCode) {

        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);

        final var kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {} {} {}",
                 kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)),
                 kv(KPI_TYPE_KEY, KPI_TYPE_IN_APP_DELIVERY),
                 kv(KPI_UUID_KEY, relevantUserExtId),
                 kv(KPI_IN_APP_DELIVERY_CODE_KEY, inAppDeliveryCode),
                 kv(KPI_UVCI_KEY, uvci)
        );
        kpiLogService.saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, KPI_TYPE_IN_APP_DELIVERY, relevantUserExtId,
                        systemSource.category)
                        .withUvci(uvci)
                        .withInAppDeliveryCode(inAppDeliveryCode)
                        .build()
        );
    }
}
