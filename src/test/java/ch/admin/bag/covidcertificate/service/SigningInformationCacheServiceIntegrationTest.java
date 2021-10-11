package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
@ActiveProfiles({"local", "mock-signing-service", "mock-printing-service"})
class SigningInformationCacheServiceIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SigningInformationCacheService signingInformationCacheService;

    @MockBean
    private SigningInformationRepository signingInformationRepository;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void setup(){
        getCache().clear();
        lenient().when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(fixture.create(SigningInformation.class));
        lenient().when(signingInformationRepository.findSigningInformation(any())).thenReturn(Collections.singletonList(fixture.create(SigningInformation.class)));
    }

    private Cache getCache(){
        return Objects.requireNonNull(cacheManager.getCache("signingInformationCache"));
    }

    @Nested
    class FindSigningInformationOnlyByCertificateType{
        @Test
        void shouldCallRepositoryAndWriteResultInCache(){
            var certificateType = fixture.create(String.class);
            var signingInformationList = Collections.singletonList(fixture.create(SigningInformation.class));
            when(signingInformationRepository.findSigningInformation(any())).thenReturn(signingInformationList);

            signingInformationCacheService.findSigningInformation(certificateType);

            verify(signingInformationRepository).findSigningInformation(certificateType);
            assertEquals(signingInformationList, Objects.requireNonNull(getCache().get(certificateType)).get());
        }

        @Test
        void shouldNotCallRepositoryIfSigningInformationAreInCache(){
            var certificateType = fixture.create(String.class);
            var signingInformationList = Collections.singletonList(fixture.create(SigningInformation.class));
            when(signingInformationRepository.findSigningInformation(any())).thenReturn(signingInformationList);

            signingInformationCacheService.findSigningInformation(certificateType);
            verify(signingInformationRepository, times(1)).findSigningInformation(certificateType);
            clearInvocations(signingInformationRepository);

            signingInformationCacheService.findSigningInformation(certificateType);

            verifyNoInteractions(signingInformationRepository);
        }
    }

    @Nested
    class FindSigningInformation{
        @Test
        void shouldCallRepositoryAndWriteResultInCache(){
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(signingInformation);

            signingInformationCacheService.findSigningInformation(certificateType, code);

            verify(signingInformationRepository).findSigningInformation(certificateType, code);
            assertEquals(signingInformation, Objects.requireNonNull(getCache().get(new SimpleKey(certificateType, code))).get());
        }

        @Test
        void shouldNotCallRepositoryIfSigningInformationAreInCache(){
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(signingInformation);

            signingInformationCacheService.findSigningInformation(certificateType, code);
            verify(signingInformationRepository, times(1)).findSigningInformation(certificateType, code);
            clearInvocations(signingInformationRepository);

            signingInformationCacheService.findSigningInformation(certificateType, code);

            verifyNoInteractions(signingInformationRepository);
        }
    }

    @Nested
    class CleanSigningInformationCache{
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
            signingInformationCacheService.cleanSigningInformationCache();

            for(int i=0; i<3; i++){
                assertNull(getCache().get(simpleKeys.get(i)));
            }
        }
    }


}