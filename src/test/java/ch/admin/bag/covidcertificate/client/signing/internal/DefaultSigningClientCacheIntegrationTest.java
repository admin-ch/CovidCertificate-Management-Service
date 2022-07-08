package ch.admin.bag.covidcertificate.client.signing.internal;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:~/test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa",
        "spring.flyway.clean-on-validation-error=true"
})
@ActiveProfiles({"local"})
@MockBean(InMemoryClientRegistrationRepository.class)
class DefaultSigningClientCacheIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DefaultSigningClient signingClient;

    @MockBean(name = "signingServiceRestTemplate")
    private RestTemplate restTemplate;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void setup(){
        getCache().clear();
        String kidUrl = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "kidUrl", kidUrl);
        ResponseEntity stringResponseEntity = mock(ResponseEntity.class);
        lenient().when(stringResponseEntity.getBody()).thenReturn(fixture.create(String.class));
        lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(stringResponseEntity);
    }

    private Cache getCache(){
        return Objects.requireNonNull(cacheManager.getCache("keyIdentifierCache"));
    }

    @Nested
    class GetKeyIdentifier {
        @Test
        void shouldCallSigningServiceAndWriteResultInCache_whenNotAlreadyInCache(){
            var certificateAlias = fixture.create(String.class);
            Integer slot = fixture.create(Integer.class);
            ResponseEntity responseEntity = mock(ResponseEntity.class);
            lenient().when(responseEntity.getBody()).thenReturn(fixture.create(String.class));
            lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);

            signingClient.getKeyIdentifier(slot, certificateAlias);

            verify(restTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
            assertEquals(responseEntity.getBody(), Objects.requireNonNull(getCache().get(new SimpleKey(slot, certificateAlias))).get());
        }

        @Test
        void shouldNotCallSigningService_whenAlreadyInCache(){
            var certificateAlias = fixture.create(String.class);
            ResponseEntity responseEntity = mock(ResponseEntity.class);
            lenient().when(responseEntity.getBody()).thenReturn(fixture.create(String.class));
            lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(responseEntity);

            Integer slot = fixture.create(Integer.class);
            signingClient.getKeyIdentifier(slot, certificateAlias);
            verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class));
            clearInvocations(restTemplate);

            signingClient.getKeyIdentifier(slot, certificateAlias);

            verifyNoInteractions(restTemplate);
        }
    }


    @Nested
    class CleanKeyIdentifierCache{
        @Test
        void shouldRemoveAllEntriesFromTheCache(){
            var simpleKeys = new ArrayList<>(fixture.collections().createCollection(SimpleKey.class, 3));
            var values = new ArrayList<>(fixture.collections().createCollection(String.class, 3));
            //setup cache
            for(int i=0; i<3; i++){
                getCache().put(simpleKeys.get(i), values.get(i));
                assertNotNull(getCache().get(simpleKeys.get(i)));
            }

            //Clean Cache
            signingClient.cleanKeyIdentifierCache();

            for(int i=0; i<3; i++){
                assertNull(getCache().get(simpleKeys.get(i)));
            }
        }
    }


}
