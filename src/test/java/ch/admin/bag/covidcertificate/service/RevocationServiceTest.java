package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.ALREADY_REVOKED_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_FRAUD_FLAG;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevocationServiceTest {
    private static final JFixture jFixture = new JFixture();
    @Mock
    private RevocationRepository revocationRepository;
    @Mock
    private KpiDataRepository kpiDataRepository;
    @Mock
    private KpiDataService kpiLogService;
    @InjectMocks
    private RevocationService revocationService;

    @Nested
    class CreateRevocation {

        @Test
        void whenCreateRevocation_thenOk() {
            // given
            FixtureCustomization.customizeRevocationDto(jFixture, false);
            RevocationDto revocationDto = jFixture.create(RevocationDto.class);
            // when
            revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud());
            // then
            verify(revocationRepository).saveAndFlush(argThat((Revocation revocation) ->
                    !revocation.isFraud() && revocationDto.getUvci().equals(revocation.getUvci())));
        }

        @Test
        void whenCreateFraudRevocation_thenOk() {
            // given
            FixtureCustomization.customizeRevocationDto(jFixture, true);
            RevocationDto revocationDto = jFixture.create(RevocationDto.class);
            // when
            revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud());
            // then
            verify(revocationRepository).saveAndFlush(argThat((Revocation revocation) ->
                    revocation.isFraud() && revocationDto.getUvci().equals(revocation.getUvci())));
        }

        @Test
        void givenUVCIExists_whenCreateRevocation_thenThrowsRevocationException() {
            // given
            RevocationDto revocationDto = jFixture.create(RevocationDto.class);
            when(revocationRepository.findByUvci(any(String.class))).thenReturn(new Revocation());
            // when then
            RevocationException exception = assertThrows(RevocationException.class,
                    () -> revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud()));
            assertEquals(DUPLICATE_UVCI, exception.getError());
        }

        @Test
        void givenExceptionIsThrown_whenCreateRevocation_thenThrowsException() {
            // given
            RevocationDto revocationDto = jFixture.create(RevocationDto.class);
            RuntimeException exception = jFixture.create(RuntimeException.class);
            when(revocationRepository.saveAndFlush(any(Revocation.class))).thenThrow(exception);
            // when then
            Exception result = assertThrows(Exception.class,
                    () -> revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud()));
            assertEquals(exception, result);
        }

        @Test
        void givenNoRevocationInDB_whenGetRevocations_thenReturnEmptyList() {
            // given
            when(revocationRepository.findNotDeletedUvcis()).thenReturn(List.of());
            // when
            List<String> result = revocationService.getRevocations();
            // then
            assertTrue(result.isEmpty());
        }

        @Test
        void givenRevocationsInDB_whenGetRevocations_thenReturnRevocations() {
            // given
            String uvci = jFixture.create(String.class);
            when(revocationRepository.findNotDeletedUvcis()).thenReturn(List.of(uvci));
            // when
            List<String> result = revocationService.getRevocations();
            // then
            assertEquals(uvci, result.get(0));
        }

        @Test
        void givenExceptionIsThrown_whenGetRevocations_thenThrowsException() {
            // given
            RuntimeException exception = jFixture.create(RuntimeException.class);
            when(revocationRepository.findNotDeletedUvcis()).thenThrow(exception);
            // when then
            Exception result = assertThrows(Exception.class, () -> revocationService.getRevocations());
            assertEquals(exception, result);
        }
    }

    @Nested
    class PerformMassRevocation {
        @Test
        void whenGivenRevocationList_ThenLoadErrors() {
            // given
            RevocationService revocationServiceSpy = Mockito.spy(revocationService);
            FixtureCustomization.customizeRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = jFixture.create(RevocationListDto.class);
            // when
            revocationServiceSpy.performMassRevocation(revocationListDto);
            // then
            verify(revocationServiceSpy).getUvcisWithErrorMessage(revocationListDto.getUvcis());
        }

        @Test
        void whenGivenValidRevocationList_ThenReturnRevoked() {
            // given
            when(revocationRepository.findByUvci(any())).thenReturn(null);
            FixtureCustomization.customizeUvciForRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = jFixture.create(RevocationListDto.class);
            // when
            RevocationListResponseDto actual = revocationService.performMassRevocation(revocationListDto);
            // then
            assertEquals(revocationListDto.getUvcis().size(), actual.getRevokedUvcis().size());
        }

        @Test
        void whenUvciIsInvalid_ReturnErrorMessage() {
            // given
            when(revocationRepository.findByUvci(any())).thenReturn(null);
            FixtureCustomization.customizeUvciForRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = new RevocationListDto(
                    List.of(new UvciForRevocationDto("invalid-uvci", true)),
                    jFixture.create(SystemSource.class)
            );
            // when
            RevocationListResponseDto actual = revocationService.performMassRevocation(revocationListDto);
            // then
            assertEquals(0, actual.getRevokedUvcis().size());
            assertEquals(1, actual.getUvcisToErrorMessage().size());
            assertTrue(actual.getUvcisToErrorMessage().values().stream().findFirst().get().contains(INVALID_UVCI.getErrorMessage()));
        }

        @Test
        void whenFraudFlagMissing_ReturnErrorMessage() {
            // given
            when(revocationRepository.findByUvci(any())).thenReturn(null);
            FixtureCustomization.customizeUvciForRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = new RevocationListDto(
                    List.of(new UvciForRevocationDto(FixtureCustomization.createUVCI(), null)),
                    jFixture.create(SystemSource.class)
            );
            // when
            RevocationListResponseDto actual = revocationService.performMassRevocation(revocationListDto);
            // then
            assertEquals(0, actual.getRevokedUvcis().size());
            assertEquals(1, actual.getUvcisToErrorMessage().size());
            assertTrue(actual.getUvcisToErrorMessage().values().stream().findFirst().get().contains(INVALID_FRAUD_FLAG.getErrorMessage()));
        }

        @Test
        void whenFraudFlagMissingAndUvciIsInvalid_ReturnErrorMessage() {
            // given
            String invalidUvci = "invalid-uvci";
            when(revocationRepository.findByUvci(any())).thenReturn(null);
            FixtureCustomization.customizeUvciForRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = new RevocationListDto(
                    List.of(new UvciForRevocationDto(invalidUvci, null)),
                    jFixture.create(SystemSource.class)
            );
            // when
            RevocationListResponseDto actual = revocationService.performMassRevocation(revocationListDto);
            // then
            assertEquals(0, actual.getRevokedUvcis().size());
            assertEquals(1, actual.getUvcisToErrorMessage().size());
            assertTrue(actual.getUvcisToErrorMessage().get(invalidUvci).contains(INVALID_UVCI.getErrorMessage()));
            assertTrue(actual.getUvcisToErrorMessage().get(invalidUvci).contains(INVALID_FRAUD_FLAG.getErrorMessage()));
        }

        @Test
        void whenUvciIsAlreadyRevoked_ReturnErrorMessage() {
            // given
            when(revocationRepository.findByUvci(any())).thenReturn(jFixture.create(Revocation.class));
            FixtureCustomization.customizeUvciForRevocationDto(jFixture, false);
            RevocationListDto revocationListDto = new RevocationListDto(
                    List.of(new UvciForRevocationDto(FixtureCustomization.createUVCI(), null)),
                    jFixture.create(SystemSource.class)
            );
            // when
            RevocationListResponseDto actual = revocationService.performMassRevocation(revocationListDto);
            // then
            assertEquals(0, actual.getRevokedUvcis().size());
            assertEquals(1, actual.getUvcisToErrorMessage().size());
            assertTrue(actual.getUvcisToErrorMessage().values().stream().findFirst().get().contains(ALREADY_REVOKED_UVCI.getErrorMessage()));
        }
    }
}
