package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetResponseDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetResponseDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetResponseDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
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
@Profile("!" + ProfileRegistry.VACCINE_SETS_SERVICE_MOCK)
public class DefaultVaccineValueSetsClient implements VaccineValueSetsClient {

    private final WebClient defaultWebClient;

    @Value("${cc-management-service.vaccine-value-set-import.vaccine-base-url}")
    private String vaccineBaseUrl;

    @Value("${cc-management-service.vaccine-value-set-import.auth-holder-base-url}")
    private String authHolderBaseUrl;

    @Value("${cc-management-service.vaccine-value-set-import.prophylaxis-base-url}")
    private String prophylaxisBaseUrl;

    /**
     *         String vaccineUrl = this.vaccineBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
     *         String authHolderUrl = this.authHolderBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
     *         String prophylaxisUrl = this.prophylaxisBaseUrl.replace("<version>", vaccineImportControl.getImportVersion());
     */

    @Override
    public Map<String, VaccineValueSetDto> getVaccineValueSet(VaccineImportControl vaccineImportControl) {
        var builder = UriComponentsBuilder
                .fromHttpUrl(this.vaccineBaseUrl)
                .uriVariables(Map.of("<version>", vaccineImportControl.getImportVersion()));

        var uri = builder.build().toString();
        log.debug("Call GitHub with url {}", uri);
        try {
            var response = defaultWebClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(VaccineValueSetResponseDto.class)
                    .block();
            if (response != null) {
                log.trace("GitHub vaccine value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
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

    @Override
    public Map<String, AuthHolderValueSetDto> getAuthHolderValueSet(VaccineImportControl vaccineImportControl) {
        var builder = UriComponentsBuilder
                .fromHttpUrl(this.authHolderBaseUrl)
                .uriVariables(Map.of("<version>", vaccineImportControl.getImportVersion()));

        var uri = builder.build().toString();
        log.debug("Call GitHub with url {}", uri);
        try {
            var response = defaultWebClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(AuthHolderValueSetResponseDto.class)
                    .block();
            if (response != null) {
                log.trace("GitHub auth holder value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
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

    @Override
    public Map<String, ProphylaxisValueSetDto> getProphylaxisValueSet(VaccineImportControl vaccineImportControl) {
        var builder = UriComponentsBuilder
                .fromHttpUrl(this.prophylaxisBaseUrl)
                .uriVariables(Map.of("<version>", vaccineImportControl.getImportVersion()));

        var uri = builder.build().toString();
        log.debug("Call GitHub with url {}", uri);
        try {
            var response = defaultWebClient
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ProphylaxisValueSetResponseDto.class)
                    .block();
            if (response != null) {
                log.trace("GitHub prophylaxis value set response: {}", response);
                log.debug("Received {} entries of valueSetId {}", response.getValueSetValues().size(), response.getValueSetId());
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
