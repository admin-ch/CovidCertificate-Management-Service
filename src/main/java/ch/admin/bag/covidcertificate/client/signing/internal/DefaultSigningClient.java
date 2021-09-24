package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;

@Service
@Slf4j
@Profile("!" + ProfileRegistry.SIGNING_SERVICE_MOCK)
public class DefaultSigningClient implements SigningClient {


    @Qualifier("signingServiceRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${cc-signing-service.url}")
    private String url;

    public DefaultSigningClient(@Qualifier("signingServiceRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public byte[] create(byte[] cosePayload, SigningInformation signingInformation) {
        var signingUrl = buildSigningUrl(signingInformation);
        log.info("Call signing service with url {}", signingUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_CBOR_VALUE));
        try {

            ResponseEntity<byte[]> result = restTemplate.exchange(signingUrl, HttpMethod.POST, new HttpEntity<>(cosePayload, headers), byte[].class);

            return result.getBody();
        }catch (RestClientException e){
            log.error("Connection with signing service {} could not be established.", url, e);
            throw e;
        }
    }

    private String buildSigningUrl(SigningInformation signingInformation){
        return new DefaultUriBuilderFactory()
                .uriString(url)
                .pathSegment(signingInformation.getAlias())
                .build().toString();
    }
}
