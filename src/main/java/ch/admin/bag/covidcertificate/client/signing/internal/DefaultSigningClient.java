package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.client.signing.SigningRequestDto;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Base64;
import java.util.Collections;

@Service
@Slf4j
@Profile("!" + ProfileRegistry.SIGNING_SERVICE_MOCK)
public class DefaultSigningClient implements SigningClient {
    private static final String KEY_IDENTIFIER_CACHE = "keyIdentifierCache";

    @Qualifier("signingServiceRestTemplate")
    private final RestTemplate restTemplate;

    @Value("${cc-signing-service.url}")
    private String url;

    @Value("${cc-signing-service.verify-url}")
    private String verifyUrl;

    @Value("${cc-signing-service.kid-url}")
    private String kidUrl;

    public DefaultSigningClient(@Qualifier("signingServiceRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public byte[] createSignature(byte[] cosePayload, SigningInformation signingInformation) {
        if(signingInformation.getCertificateAlias() == null ||signingInformation.getCertificateAlias().isBlank()){
            return createSignatureWithKeyIdentifier(cosePayload, signingInformation);
        } else {
            return createSignatureWithCertificateAlias(cosePayload, signingInformation);
        }
    }

    //TODO: keyIdentifier should be deleted. It is deprecated and used only for backwards compatibility.
    /*
    * @deprecated This should be deleted and the createSignatureWithCertificateAlias should be used instead
     */
    @Deprecated(since = "2.7.0")
    protected byte[] createSignatureWithKeyIdentifier(byte[] cosePayload, SigningInformation signingInformation) {
        var signingUrl = buildSigningUrl(url, signingInformation.getAlias());
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


    protected byte[] createSignatureWithCertificateAlias(byte[] cosePayload, SigningInformation signingInformation) {
        var signingRequestDto = new SigningRequestDto(Base64.getEncoder().encodeToString(cosePayload),
                                                      signingInformation.getAlias(),
                                                      signingInformation.getCertificateAlias());
        long start = System.currentTimeMillis();
        log.info("Call signing service with url {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        try {
            ResponseEntity<byte[]> result = restTemplate.exchange(url, HttpMethod.POST,
                                                                  new HttpEntity<>(signingRequestDto, headers),
                                                                  byte[].class);
            long end = System.currentTimeMillis();
            log.info("Call of signing service finished with result {} within {} ms.", result.getStatusCode(),
                     end - start);
            return result.getBody();
        }catch (RestClientException e){
            log.error("Connection with signing service {} could not be established.", url, e);
            throw e;
        }
    }

    public boolean verifySignature(VerifySignatureRequestDto verifySignatureRequestDto) {
        log.info("Call signing service with url {}", verifyUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
        try {
            ResponseEntity<Boolean> result = restTemplate.exchange(verifyUrl, HttpMethod.POST, new HttpEntity<>(verifySignatureRequestDto, headers), boolean.class);
            return result.getBody();
        }catch (RestClientException e){
            log.error("Connection with signing service {} could not be established.", verifyUrl, e);
            throw e;
        }
    }

    @Cacheable(KEY_IDENTIFIER_CACHE)
    public String getKeyIdentifier(String certificateAlias){
        var getKeyUrl = buildSigningUrl(kidUrl, certificateAlias);
        long start = System.currentTimeMillis();
        log.info("Call signing service to retrieve key identifier for certificate {}.", certificateAlias);

        try {
            ResponseEntity<String> result = restTemplate.exchange(getKeyUrl, HttpMethod.GET,
                                                                  new HttpEntity<>(new HttpHeaders()), String.class);
            long end = System.currentTimeMillis();
            log.info("Call of signing service finished with result {} within {} ms.", result.getStatusCode(),
                     end - start);
            return result.getBody();
        }catch (RestClientException e){
            log.error("Connection with signing service {} could not be established.", url, e);
            throw e;
        }
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = KEY_IDENTIFIER_CACHE, allEntries = true)
    public void cleanKeyIdentifierCache() {
        log.info("Cleaning cache of key identifier");
    }

    private String buildSigningUrl(String url, String pathSegment){
        return new DefaultUriBuilderFactory()
                .uriString(url)
                .pathSegment(pathSegment)
                .build().toString();
    }
}
