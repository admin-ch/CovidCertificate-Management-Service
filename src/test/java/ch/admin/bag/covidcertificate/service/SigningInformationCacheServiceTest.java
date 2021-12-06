package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigningInformationCacheServiceTest {
    @InjectMocks
    private SigningInformationCacheService signingInformationCacheService;

    @Mock
    private SigningInformationRepository signingInformationRepository;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    private void setup(){
        lenient().when(signingInformationRepository.findSigningInformation(any(), any(), any())).thenReturn(fixture.create(SigningInformation.class));
        lenient().when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(Collections.singletonList(fixture.create(SigningInformation.class)));
    }


    @Nested
    class FindSigningInformationOnlyByCertificateType{
        @Test
        void shouldCallRepositoryWithCorrectParameter(){
            var certificateType = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);

            signingInformationCacheService.findSigningInformation(certificateType, validAt);

            verify(signingInformationRepository).findSigningInformation(certificateType, validAt);
        }

        @Test
        void shouldReturnLoadedSigningInformation(){
            var certificateType = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformationList = Collections.singletonList(fixture.create(SigningInformation.class));
            when(signingInformationRepository.findSigningInformation(any(), any())).thenReturn(signingInformationList);

            var actual = signingInformationCacheService.findSigningInformation(certificateType, validAt);

            assertEquals(signingInformationList, actual);
        }
    }

    @Nested
    class FindSigningInformation{
        @Test
        void shouldCallRepositoryWithCorrectCertificateType(){
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);

            signingInformationCacheService.findSigningInformation(certificateType, code, validAt);

            verify(signingInformationRepository).findSigningInformation(eq(certificateType), any(), any());
        }

        @Test
        void shouldCallRepositoryWithCorrectCode(){
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);

            signingInformationCacheService.findSigningInformation(certificateType, code, validAt);

            verify(signingInformationRepository).findSigningInformation(any(), eq(code), any());
        }
        @Test
        void shouldNotCallRepositoryIfSigningInformationAreInCache(){
            var certificateType = fixture.create(String.class);
            var code = fixture.create(String.class);
            var validAt = fixture.create(LocalDate.class);
            var signingInformation = fixture.create(SigningInformation.class);
            when(signingInformationRepository.findSigningInformation(any(), any(), any())).thenReturn(signingInformation);

            var actual = signingInformationCacheService.findSigningInformation(certificateType, code, validAt);

            assertEquals(signingInformation, actual);
        }
    }
}