package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.service.domain.SigningCertificateCategory;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.AMBIGUOUS_SIGNING_CERTIFICATE;
import static ch.admin.bag.covidcertificate.api.Constants.SIGNING_CERTIFICATE_MISSING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SigningInformationServiceTest {
    @InjectMocks
    private SigningInformationService signingInformationService;
    @Mock
    private SigningInformationCacheService signingInformationCacheService;

    private final JFixture fixture = new JFixture();


    @BeforeEach
    void setup() {
        lenient().when(signingInformationCacheService.findSigningInformation(any(), any(), any()))
                 .thenReturn(fixture.create(SigningInformationDto.class));
        lenient().when(signingInformationCacheService.findSigningInformation(any(), any()))
                 .thenReturn(Collections.singletonList(fixture.create(
                         SigningInformationDto.class)));
    }

    @Nested
    class GetVaccinationSigningInformation{
        @Test
        void shouldLoadSigningInformationFromCacheWithCorrectSigningCertificateCategory(){
            signingInformationService.getVaccinationSigningInformation(fixture.create(VaccinationCertificateCreateDto.class));
            verify(signingInformationCacheService).findSigningInformation(eq(SigningCertificateCategory.VACCINATION.value), any(), any());
        }

        @Test
        void shouldLoadSigningInformationFromCacheWithCorrectVaccinationCode(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            var vaccinationCode = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();

            signingInformationService.getVaccinationSigningInformation(createDto);
            verify(signingInformationCacheService).findSigningInformation(any(), eq(vaccinationCode), any());
        }

        @Test
        void shouldReturnLoadedSigningInformation(){
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationCacheService.findSigningInformation(any(), any(), any())).thenReturn(signingInformation);

            var actual = signingInformationService.getVaccinationSigningInformation(fixture.create(VaccinationCertificateCreateDto.class));

            assertEquals(signingInformation, actual);
        }

        @Test
        void shouldThrowCreateCertificateExceptionWith5xxErrorCode_ifNoSigningInformationIsFound(){
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            when(signingInformationCacheService.findSigningInformation(any(), any(), any())).thenReturn(null);

            var exception = assertThrows(CreateCertificateException.class,
                    () -> signingInformationService.getVaccinationSigningInformation(createDto)
            );

            assertEquals(SIGNING_CERTIFICATE_MISSING, exception.getError());
            assertEquals(559, exception.getError().getErrorCode());
        }
    }

    @Nested
    class GetTestSigningInformation{
        @Test
        void shouldLoadSigningInformationFromCacheWithCorrectSigningCertificateCategory(){
            signingInformationService.getTestSigningInformation();
            verify(signingInformationCacheService).findSigningInformation(eq(SigningCertificateCategory.TEST.value), any());
        }

        @Test
        void shouldReturnLoadedSigningInformation(){
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(Collections.singletonList(signingInformation));

            var actual = signingInformationService.getTestSigningInformation();

            assertEquals(signingInformation, actual);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowCreateCertificateExceptionWith5xxErrorCode_ifNoSigningInformationIsFound(
                List<SigningInformationDto> signingInformationList) {
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(
                    signingInformationList);

            var exception = assertThrows(CreateCertificateException.class,
                                         () -> signingInformationService.getTestSigningInformation()
            );

            assertEquals(SIGNING_CERTIFICATE_MISSING, exception.getError());
            assertEquals(559, exception.getError().getErrorCode());
        }

        @Test
        void shouldThrowCreateCertificateExceptionWith5xxErrorCode_ifMultipleSigningInformationAreFound() {
            var signingInformationList = new ArrayList<>(
                    fixture.collections().createCollection(SigningInformationDto.class));
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(
                    signingInformationList);

            var exception = assertThrows(CreateCertificateException.class,
                                         () -> signingInformationService.getTestSigningInformation()
            );

            assertEquals(AMBIGUOUS_SIGNING_CERTIFICATE, exception.getError());
            assertEquals(560, exception.getError().getErrorCode());
        }
    }

    @Nested
    class GetRecoverySigningInformation{
        @Test
        void shouldLoadSigningInformationFromCacheWithCorrectSigningCertificateCategory_forCHCertificates(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var recoveryDataDto = fixture.create(RecoveryCertificateDataDto.class);
            ReflectionTestUtils.setField(recoveryDataDto, "countryOfTest", "CH");
            ReflectionTestUtils.setField(createDto, "recoveryInfo", Collections.singletonList(recoveryDataDto));

            signingInformationService.getRecoverySigningInformation(createDto);
            verify(signingInformationCacheService).findSigningInformation(eq(SigningCertificateCategory.RECOVERY_CH.value), any());
        }

        @Test
        void shouldLoadSigningInformationFromCacheWithCorrectSigningCertificateCategory_forNonCHCertificates(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            signingInformationService.getRecoverySigningInformation(createDto);
            verify(signingInformationCacheService).findSigningInformation(eq(SigningCertificateCategory.RECOVERY_NON_CH.value), any());
        }

        @Test
        void shouldReturnLoadedSigningInformation(){
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(Collections.singletonList(signingInformation));

            var actual = signingInformationService.getRecoverySigningInformation(createDto);

            assertEquals(signingInformation, actual);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowCreateCertificateExceptionWith5xxErrorCode_ifNoSigningInformationIsFound(
                List<SigningInformationDto> signingInformationList) {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(
                    signingInformationList);

            var exception = assertThrows(CreateCertificateException.class,
                                         () -> signingInformationService.getRecoverySigningInformation(createDto)
            );

            assertEquals(SIGNING_CERTIFICATE_MISSING, exception.getError());
            assertEquals(559, exception.getError().getErrorCode());
        }

        @Test
        void shouldThrowCreateCertificateExceptionWith5xxErrorCode_ifMultipleSigningInformationAreFound() {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            var signingInformationList = new ArrayList<>(
                    fixture.collections().createCollection(SigningInformationDto.class));
            when(signingInformationCacheService.findSigningInformation(any(), any())).thenReturn(
                    signingInformationList);

            var exception = assertThrows(CreateCertificateException.class,
                                         () -> signingInformationService.getRecoverySigningInformation(createDto)
            );

            assertEquals(AMBIGUOUS_SIGNING_CERTIFICATE, exception.getError());
            assertEquals(560, exception.getError().getErrorCode());
        }
    }
}