package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.SigningInformationMapper;
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
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
class SigningInformationCacheServiceIntegrationTest {
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private SigningInformationCacheService signingInformationCacheService;

    @MockBean
    private SigningInformationRepository signingInformationRepository;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void setup() {
        getCache().clear();
        lenient().when(signingInformationRepository.findSigningInformation(any(), any(), any()))
                .thenReturn(fixture.create(SigningInformation.class));
        lenient().when(signingInformationRepository.findSigningInformation(any(), any()))
                .thenReturn(Collections.singletonList(fixture.create(SigningInformation.class)));
    }

    private Cache getCache() {
        return Objects.requireNonNull(cacheManager.getCache("signingInformationCache"));
    }

    @Nested
    class FindSigningInformationOnlyByCertificateType {
        @Test
        void shouldCallRepositoryAndWriteResultInCache_ifNotInCache() {
            var certificateType = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformationList = Collections.singletonList(
                    fixture.create(SigningInformation.class));
            var expectedDtoList = SigningInformationMapper.fromEntityList(signingInformationList);
            when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(signingInformationList);

            signingInformationCacheService.findSigningInformation(certificateType, validAt);

            verify(signingInformationRepository).findSigningInformation(certificateType, validAt);
            assertEquals(
                    expectedDtoList,
                    Objects.requireNonNull(getCache().get(new SimpleKey(certificateType, validAt))).get());
        }

        @Test
        void shouldNotCallRepositoryIfSigningInformationAreInCache() {
            var certificateType = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformationList = Collections.singletonList(fixture.create(SigningInformation.class));
            when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(signingInformationList);

            signingInformationCacheService.findSigningInformation(certificateType, validAt);
            verify(signingInformationRepository, times(1)).findSigningInformation(certificateType, validAt);
            clearInvocations(signingInformationRepository);

            signingInformationCacheService.findSigningInformation(certificateType, validAt);

            verifyNoInteractions(signingInformationRepository);
        }
    }

    @Nested
    class FindSigningInformation {
        @Test
        void shouldCallRepositoryAndWriteResultInCache_ifNotInCache() {
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformation = fixture.create(SigningInformation.class);
            var expectedDto = SigningInformationMapper.fromEntity(signingInformation);
            when(signingInformationRepository.findSigningInformation(any(), any(), any())).thenReturn(
                    signingInformation);

            signingInformationCacheService.findSigningInformation(certificateType, code, validAt);

            verify(signingInformationRepository).findSigningInformation(eq(certificateType), eq(code), any());
            assertEquals(expectedDto,
                    Objects.requireNonNull(getCache().get(new SimpleKey(certificateType, code, validAt))).get());
        }

        @Test
        void shouldNotCallRepositoryIfSigningInformationAreInCache() {
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationRepository.findSigningInformation(any(), any(), any())).thenReturn(signingInformation);

            signingInformationCacheService.findSigningInformation(certificateType, code, validAt);
            verify(signingInformationRepository, times(1)).findSigningInformation(certificateType, code, validAt);
            clearInvocations(signingInformationRepository);

            signingInformationCacheService.findSigningInformation(certificateType, code, validAt);

            verifyNoInteractions(signingInformationRepository);
        }
    }

    @Nested
    class CleanSigningInformationCache {
        @Test
        void shouldRemoveAllEntriesFromTheCache() {
            var simpleKeys = new ArrayList<>(fixture.collections().createCollection(SimpleKey.class, 3));
            var values = new ArrayList<>(fixture.collections().createCollection(String.class, 3));
            //setup cache
            for (int i = 0; i < 3; i++) {
                getCache().put(simpleKeys.get(i), values.get(i));
                assertNotNull(getCache().get(simpleKeys.get(i)));
            }

            //Clean Cache
            signingInformationCacheService.cleanSigningInformationCache();

            for (int i = 0; i < 3; i++) {
                assertNull(getCache().get(simpleKeys.get(i)));
            }
        }
    }


}