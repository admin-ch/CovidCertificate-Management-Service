package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.vaccines.VaccineValueSetsClient;
import ch.admin.bag.covidcertificate.domain.AuthHolder;
import ch.admin.bag.covidcertificate.domain.AuthHolderRepository;
import ch.admin.bag.covidcertificate.domain.DisplayNameModification;
import ch.admin.bag.covidcertificate.domain.DisplayNameModificationRepository;
import ch.admin.bag.covidcertificate.domain.Prophylaxis;
import ch.admin.bag.covidcertificate.domain.ProphylaxisRepository;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import ch.admin.bag.covidcertificate.domain.VaccineImportControlRepository;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import ch.admin.bag.covidcertificate.domain.ValueSetUpdateLogRepository;
import ch.admin.bag.covidcertificate.domain.enums.EntityType;
import com.flextrade.jfixture.JFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class VaccineImportServiceTest {

    @InjectMocks
    private VaccineImportService vaccineImportService;

    @Mock
    private VaccineValueSetsClient valueSetsClient;

    @Mock
    private ValueSetUpdateLogRepository valueSetUpdateLogRepository;

    @Mock
    private VaccineImportControlRepository vaccineImportControlRepository;

    @Mock
    private DisplayNameModificationRepository displayNameModificationRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private AuthHolderRepository authHolderRepository;

    @Mock
    private ProphylaxisRepository prophylaxisRepository;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    void setUp() {
        clearInvocations(
                vaccineRepository,
                authHolderRepository,
                prophylaxisRepository,
                vaccineImportControlRepository,
                valueSetUpdateLogRepository);
        lenient().when(valueSetsClient.getVaccineValueSet(any())).thenReturn(
                fixture.collections().createMap(String.class, VaccineValueSetDto.class));
        lenient().when(valueSetsClient.getAuthHolderValueSet(any())).thenReturn(
                fixture.collections().createMap(String.class, AuthHolderValueSetDto.class));
        lenient().when(valueSetsClient.getProphylaxisValueSet(any())).thenReturn(
                fixture.collections().createMap(String.class, ProphylaxisValueSetDto.class));

        lenient().when(vaccineRepository.findAllCodes()).thenReturn(
                fixture.collections().createCollection(List.class, String.class));
        DisplayNameModification vaccineModification = fixture.create(DisplayNameModification.class);
        lenient().when(displayNameModificationRepository.findByCodeAndEntityType(any(), eq(EntityType.VACCINE)))
                .thenReturn(Optional.of(vaccineModification));

        lenient().when(authHolderRepository.findAllCodes()).thenReturn(
                fixture.collections().createCollection(List.class, String.class));
        DisplayNameModification authHolderModification = fixture.create(DisplayNameModification.class);
        lenient().when(displayNameModificationRepository.findByCodeAndEntityType(any(), eq(EntityType.AUTH_HOLDER)))
                .thenReturn(Optional.of(authHolderModification));

        lenient().when(prophylaxisRepository.findAllCodes()).thenReturn(
                fixture.collections().createCollection(List.class, String.class));
        DisplayNameModification prophylaxisModification = fixture.create(DisplayNameModification.class);
        lenient().when(displayNameModificationRepository.findByCodeAndEntityType(any(), eq(EntityType.PROPHYLAXIS)))
                .thenReturn(Optional.of(prophylaxisModification));

        VaccineImportControl vaccineImportControl = new VaccineImportControl("2.9.0", LocalDate.now(), false);
        lenient().when(vaccineImportControlRepository.findByImportDateLessThanEqualAndDoneFalse(any()))
                .thenReturn(Optional.of(vaccineImportControl));
    }

    @Test
    void normal_flow_of_updateValueSetOfVaccines_ifFindByCodeReturnsAObject() {
        // given
        // additionally to the mocks for all tests in setUp()
        lenient().when(vaccineRepository.findByCode(any())).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(authHolderRepository.findByCode(any())).thenReturn(fixture.create(AuthHolder.class));
        lenient().when(prophylaxisRepository.findByCode(any())).thenReturn(fixture.create(Prophylaxis.class));
        // when
        boolean result = vaccineImportService.updateValueSetOfVaccines(LocalDate.now());
        // then
        assertThat(result).isTrue();
        verify(vaccineRepository, atLeast(3)).save(any());
        verify(authHolderRepository, atLeast(3)).save(any());
        verify(prophylaxisRepository, atLeast(3)).save(any());
        verify(vaccineImportControlRepository, atLeast(1)).save(any());
        // save count of valueSetUpdateLogRepository is 18!
        verify(valueSetUpdateLogRepository, atLeast(18)).save(any());
    }

    @Test
    void create_flow_of_updateValueSetOfVaccines_ifFindByCodeReturnsNoObject() {
        // given
        // additionally to the mocks for all tests in setUp()
        lenient().when(vaccineRepository.findByCode(any())).thenReturn(Collections.emptyList());
        lenient().when(authHolderRepository.findByCode(any())).thenReturn(null);
        lenient().when(prophylaxisRepository.findByCode(any())).thenReturn(null);
        // when
        boolean result = vaccineImportService.updateValueSetOfVaccines(LocalDate.now());
        // then
        assertThat(result).isTrue();
        verify(vaccineRepository, atLeast(3)).save(any());
        verify(authHolderRepository, atLeast(3)).save(any());
        verify(prophylaxisRepository, atLeast(3)).save(any());
        verify(vaccineImportControlRepository, atLeast(1)).save(any());
        // save count of valueSetUpdateLogRepository is 18!
        verify(valueSetUpdateLogRepository, atMost(18)).save(any());
    }

    @Test
    void exceptional_flow_of_updateValueSetOfVaccines_ifVaccineRepositorySaveThrowsException() {
        // given
        // additionally to the mocks for all tests in setUp()
        lenient().when(vaccineRepository.findByCode(any())).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(authHolderRepository.findByCode(any())).thenReturn(fixture.create(AuthHolder.class));
        lenient().when(prophylaxisRepository.findByCode(any())).thenReturn(fixture.create(Prophylaxis.class));
        when(vaccineRepository.save(any())).thenThrow(new RuntimeException("Mocked RuntimeException saving a vaccine"));
        // when
        boolean result = vaccineImportService.updateValueSetOfVaccines(LocalDate.now());
        // then
        assertThat(result).isTrue();
        verify(authHolderRepository, atLeast(3)).save(any());
        verify(prophylaxisRepository, atLeast(3)).save(any());
        verify(vaccineImportControlRepository, atLeast(1)).save(any());
        // save count of valueSetUpdateLogRepository is 12!
        // 3 auth holder updates and 3 prophylaxis updates plus 6 deletions (3 auth holder deletions and 3 prophylaxis deletions)
        // this is as the mocked codes differ
        verify(valueSetUpdateLogRepository, atLeast(12)).save(any());
    }

    @Test
    void exceptional_flow_of_updateValueSetOfVaccines_ifAuthHolderRepositorySaveThrowsException() {
        // given
        // additionally to the mocks for all tests in setUp()
        lenient().when(vaccineRepository.findByCode(any())).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(authHolderRepository.findByCode(any())).thenReturn(fixture.create(AuthHolder.class));
        lenient().when(prophylaxisRepository.findByCode(any())).thenReturn(fixture.create(Prophylaxis.class));
        when(authHolderRepository.save(any())).thenThrow(new RuntimeException("Mocked RuntimeException saving an auth holder"));
        // when
        boolean result = vaccineImportService.updateValueSetOfVaccines(LocalDate.now());
        // then
        assertThat(result).isTrue();
        verify(vaccineRepository, atLeast(3)).save(any());
        verify(prophylaxisRepository, atLeast(3)).save(any());
        verify(vaccineImportControlRepository, atLeast(1)).save(any());
        // save count of valueSetUpdateLogRepository is 12!
        // 3 vaccine updates and 3 prophylaxis updates plus 6 deletions (3 vaccine deletions and 3 prophylaxis deletions)
        // this is as the mocked codes differ
        verify(valueSetUpdateLogRepository, atLeast(12)).save(any());
    }

    @Test
    void exceptional_flow_of_updateValueSetOfVaccines_ifProphylaxisRepositorySaveThrowsException() {
        // given
        // additionally to the mocks for all tests in setUp()
        lenient().when(vaccineRepository.findByCode(any())).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(authHolderRepository.findByCode(any())).thenReturn(fixture.create(AuthHolder.class));
        lenient().when(prophylaxisRepository.findByCode(any())).thenReturn(fixture.create(Prophylaxis.class));
        when(prophylaxisRepository.save(any())).thenThrow(new RuntimeException("Mocked RuntimeException saving a vaccine"));
        // when
        boolean result = vaccineImportService.updateValueSetOfVaccines(LocalDate.now());
        // then
        assertThat(result).isTrue();
        verify(vaccineRepository, atLeast(3)).save(any());
        verify(authHolderRepository, atLeast(3)).save(any());
        verify(vaccineImportControlRepository, atLeast(1)).save(any());
        // save count of valueSetUpdateLogRepository is 12!
        // 3 vaccine updates and 3 auth holder updates plus 6 deletions (3 vaccine deletions and 3 auth holder deletions)
        // this is as the mocked codes differ
        verify(valueSetUpdateLogRepository, atLeast(12)).save(any());
    }
}

