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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!" + ProfileRegistry.VACCINE_SETS_SERVICE_MOCK)
public class DefaultVaccineValueSetsClient implements VaccineValueSetsClient {

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
        String vaccineUrl = this.vaccineBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
        log.debug("Call GitHub with url {}", vaccineUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(vaccineUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            VaccineValueSetResponseDto response = objectMapper.readValue(answer.body(), VaccineValueSetResponseDto.class);
            if (response != null) {
                log.trace("GitHub vaccine value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error("Response from {} is null", vaccineUrl);
                Thread.currentThread().interrupt();
            }
        } catch (Exception e) {
            log.error("Request to {} failed", vaccineUrl, e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    public Map<String, AuthHolderValueSetDto> getAuthHolderValueSet(VaccineImportControl vaccineImportControl) {
        String authHolderUrl = this.authHolderBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
        log.debug("Call GitHub with url {}", authHolderUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(authHolderUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            AuthHolderValueSetResponseDto response = objectMapper.readValue(answer.body(), AuthHolderValueSetResponseDto.class);
            if (response != null) {
                log.trace("GitHub auth holder value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error("Response from {} is null", authHolderUrl);
                throw new IllegalStateException("Response is null");
            }
        } catch (Exception e) {
            log.error("Request to {} failed", authHolderUrl, e);
            Thread.currentThread().interrupt();
        }
        return null;
    }

    @Override
    public Map<String, ProphylaxisValueSetDto> getProphylaxisValueSet(VaccineImportControl vaccineImportControl) {
        String prophylaxisUrl = this.prophylaxisBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
        log.debug("Call GitHub with url {}", prophylaxisUrl);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(prophylaxisUrl))
                    .timeout(Duration.of(readTimeout, ChronoUnit.SECONDS))
                    .GET()
                    .build();
            HttpResponse<String> answer = this.proxyAwareHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ProphylaxisValueSetResponseDto response = objectMapper.readValue(answer.body(), ProphylaxisValueSetResponseDto.class);
            if (response != null) {
                log.trace("GitHub prophylaxis value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
                return response.getValueSetValues();
            } else {
                log.error("Response from {} is null", prophylaxisUrl);
                throw new IllegalStateException("Response is null");
            }
        } catch (Exception e) {
            log.error("Request to {} failed", prophylaxisUrl, e);
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
