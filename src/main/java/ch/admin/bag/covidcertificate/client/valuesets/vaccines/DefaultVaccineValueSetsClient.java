package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetResponseDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetResponseDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetResponseDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!" + ProfileRegistry.VACCINE_SETS_SERVICE_MOCK)
public class DefaultVaccineValueSetsClient implements VaccineValueSetsClient {

    public static final String MESSAGE_CALL_GIT_HUB_WITH_URL = "Call GitHub with url {}";
    public static final String MESSAGE_GIT_HUB_VACCINE_VALUE_SET_RESPONSE = "GitHub vaccine value set response: {}";
    public static final String MESSAGE_RECEIVED_ENTRIES_OF_VALUE_SET_ID = "Received {} entries of valueSetId {}";
    public static final String MESSAGE_GIT_HUB_AUTH_HOLDER_VALUE_SET_RESPONSE = "GitHub auth holder value set response: {}";
    public static final String MESSAGE_GIT_HUB_PROPHYLAXIS_VALUE_SET_RESPONSE = "GitHub prophylaxis value set response: {}";
    public static final String MESSAGE_RESPONSE_FROM_IS_NULL = "Response from {} is null";
    public static final String MESSAGE_RESPONSE_IS_NULL = "Response is null";
    public static final String MESSAGE_REQUEST_TO_FAILED = "Request to {} failed";
    public static final String VERSION_PLACEHOLDER = "<version>";
    private final HttpClient proxyAwareHttpClient;

    private final ObjectMapper objectMapper;

    @Value("${cc-management-service.vaccine-value-set-import.vaccine-base-url}")
    private String vaccineBaseUrl;

    @Value("${cc-management-service.vaccine-value-set-import.auth-holder-base-url}")
    private String authHolderBaseUrl;

    @Value("${cc-management-service.vaccine-value-set-import.prophylaxis-base-url}")
    private String prophylaxisBaseUrl;

    @Value("${cc-management-service.rest.readTimeoutSeconds}")
    private int readTimeout;

    @Override
    public Map<String, VaccineValueSetDto> getVaccineValueSet(VaccineImportControl vaccineImportControl) {
        String vaccineUrl = this.vaccineBaseUrl.replace(VERSION_PLACEHOLDER, vaccineImportControl.getImportVersion());
        log.debug(MESSAGE_CALL_GIT_HUB_WITH_URL, vaccineUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(vaccineUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            VaccineValueSetResponseDto response = objectMapper.readValue(answer.body(), VaccineValueSetResponseDto.class);
            if (response != null) {
                log.trace(MESSAGE_GIT_HUB_VACCINE_VALUE_SET_RESPONSE, response);
                log.debug(MESSAGE_RECEIVED_ENTRIES_OF_VALUE_SET_ID, response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error(MESSAGE_RESPONSE_FROM_IS_NULL, vaccineUrl);
                throw new IllegalStateException(MESSAGE_RESPONSE_IS_NULL);
            }
        } catch (Exception e) {
            log.error(MESSAGE_REQUEST_TO_FAILED, vaccineUrl, e);
            Thread.currentThread().interrupt();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AuthHolderValueSetDto> getAuthHolderValueSet(VaccineImportControl vaccineImportControl) {
        String authHolderUrl = this.authHolderBaseUrl.replace(VERSION_PLACEHOLDER, vaccineImportControl.getImportVersion());
        log.debug(MESSAGE_CALL_GIT_HUB_WITH_URL, authHolderUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(authHolderUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            AuthHolderValueSetResponseDto response = objectMapper.readValue(answer.body(), AuthHolderValueSetResponseDto.class);
            if (response != null) {
                log.trace(MESSAGE_GIT_HUB_AUTH_HOLDER_VALUE_SET_RESPONSE, response);
                log.debug(MESSAGE_RECEIVED_ENTRIES_OF_VALUE_SET_ID, response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error(MESSAGE_RESPONSE_FROM_IS_NULL, authHolderUrl);
                throw new IllegalStateException(MESSAGE_RESPONSE_IS_NULL);
            }
        } catch (Exception e) {
            log.error(MESSAGE_REQUEST_TO_FAILED, authHolderUrl, e);
            Thread.currentThread().interrupt();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ProphylaxisValueSetDto> getProphylaxisValueSet(VaccineImportControl vaccineImportControl) {
        String prophylaxisUrl = this.prophylaxisBaseUrl.replace(VERSION_PLACEHOLDER, vaccineImportControl.getImportVersion());
        log.debug(MESSAGE_CALL_GIT_HUB_WITH_URL, prophylaxisUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(prophylaxisUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ProphylaxisValueSetResponseDto response = objectMapper.readValue(answer.body(), ProphylaxisValueSetResponseDto.class);
            if (response != null) {
                log.trace(MESSAGE_GIT_HUB_PROPHYLAXIS_VALUE_SET_RESPONSE, response);
                log.debug(MESSAGE_RECEIVED_ENTRIES_OF_VALUE_SET_ID, response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error(MESSAGE_RESPONSE_FROM_IS_NULL, prophylaxisUrl);
                throw new IllegalStateException(MESSAGE_RESPONSE_IS_NULL);
            }
        } catch (Exception e) {
            log.error(MESSAGE_REQUEST_TO_FAILED, prophylaxisUrl, e);
            Thread.currentThread().interrupt();
        }
        return Collections.emptyMap();
    }
}
