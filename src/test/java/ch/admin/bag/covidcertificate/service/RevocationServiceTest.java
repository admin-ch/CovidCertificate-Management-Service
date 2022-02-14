package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevocationServiceTest {
    private static final JFixture jFixture = new JFixture();
    @Mock
    private RevocationRepository revocationRepository;
    @InjectMocks
    private RevocationService revocationService;

    @Test
    void whenCreateRevocation_thenOk() {
        FixtureCustomization.customizeRevocationDto(jFixture, false);
        // given
        RevocationDto revocationDto = jFixture.create(RevocationDto.class);
        when(revocationRepository.saveAndFlush(any(Revocation.class))).thenReturn(any(Revocation.class));
        // when
        revocationService.createRevocation(revocationDto);
        // then
        verify(revocationRepository).saveAndFlush(argThat((Revocation revocation) ->
                revocation.isFraud()==false && revocationDto.getUvci().equals(revocation.getUvci())));
    }

    @Test
    void whenCreateFraudRevocation_thenOk() {
        FixtureCustomization.customizeRevocationDto(jFixture, true);
        // given
        RevocationDto revocationDto = jFixture.create(RevocationDto.class);
        when(revocationRepository.saveAndFlush(any(Revocation.class))).thenReturn(any(Revocation.class));
        // when
        revocationService.createRevocation(revocationDto);
        // then
        verify(revocationRepository).saveAndFlush(argThat((Revocation revocation) ->
                revocation.isFraud()==true && revocationDto.getUvci().equals(revocation.getUvci())));
    }

    @Test
    void givenUVCIExists_whenCreateRevocation_thenThrowsRevocationException() {
        // given
        RevocationDto revocationDto = jFixture.create(RevocationDto.class);
        when(revocationRepository.findByUvci(any(String.class))).thenReturn(new Revocation());
        // when then
        RevocationException exception = assertThrows(RevocationException.class,
                () -> revocationService.createRevocation(revocationDto));
        assertEquals(DUPLICATE_UVCI, exception.getError());
    }

    @Test
    void givenExceptionIsThrown_whenCreateRevocation_thenThrowsException() {
        // given
        RevocationDto revocationDto = jFixture.create(RevocationDto.class);
        RuntimeException exception = jFixture.create(RuntimeException.class);
        when(revocationRepository.saveAndFlush(any(Revocation.class))).thenThrow(exception);
        // when then
        Exception result = assertThrows(Exception.class, () -> revocationService.createRevocation(revocationDto));
        assertEquals(exception, result);
    }

    @Test
    void givenNoRevocationInDB_whenGetRevocations_thenReturnEmptyList() {
        // given
        when(revocationRepository.findAllUvcis()).thenReturn(List.of());
        // when
        List<String> result = revocationService.getRevocations();
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void givenRevocationsInDB_whenGetRevocations_thenReturnRevocations() {
        // given
        String uvci = jFixture.create(String.class);
        when(revocationRepository.findAllUvcis()).thenReturn(List.of(uvci));
        // when
        List<String> result = revocationService.getRevocations();
        // then
        assertEquals(uvci, result.get(0));
    }

    @Test
    void givenExceptionIsThrown_whenGetRevocations_thenThrowsException() {
        // given
        RuntimeException exception = jFixture.create(RuntimeException.class);
        when(revocationRepository.findAllUvcis()).thenThrow(exception);
        // when then
        Exception result = assertThrows(Exception.class, () -> revocationService.getRevocations());
        assertEquals(exception, result);
    }
}
