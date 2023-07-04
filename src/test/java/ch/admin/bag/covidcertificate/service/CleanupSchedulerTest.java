package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.config.cleanup.Cleanup;
import ch.admin.bag.covidcertificate.config.cleanup.CleanupConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

import java.io.EOFException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanupSchedulerTest {
    @InjectMocks
    private CleanupScheduler cleanupScheduler;
    @Mock
    private CleanupService cleanupService;
    @Mock
    private CleanupConfig cleanupConfig;


    @Test
    void processCleanup_noSpotsConfigured() {
        when(cleanupConfig.getSpots()).thenReturn(Collections.emptyMap());

        cleanupScheduler.processCleanup();

        verify(cleanupConfig).getSpots();
    }

    @Test
    void processCleanup_oneSpotConfigured() {
        Map<String, Cleanup> map = new TreeMap<>();
        map.put("name1", new Cleanup());
        map.put("name2", new Cleanup());
        when(cleanupConfig.getSpots()).thenReturn(map);

        CleanupService.CleaningEffort effort1 = new CleanupService.CleaningEffort("name1",
                "countQuery", "deleteUntilQuery", "deleteUntilBatchQuery", 5,
                null, null);

        CleanupService.CleaningEffort effort2 = new CleanupService.CleaningEffort("name2",
                "countQuery", "deleteUntilQuery", "deleteUntilBatchQuery", 5,
                null, null);

        when(cleanupService.create(eq("name1"), any(Cleanup.class))).thenReturn(effort1);
        when(cleanupService.getCount(eq(effort1), any(LocalDate.class))).thenReturn(0L);
        doNothing().when(cleanupService).destroy(eq(effort1));

        when(cleanupService.create(eq("name2"), any(Cleanup.class))).thenReturn(effort2);
        when(cleanupService.getCount(eq(effort2), any(LocalDate.class))).thenReturn(5L);
        when(cleanupService.delete(eq(effort2), any(LocalDate.class), eq(true))).thenReturn(0L);
        when(cleanupService.delete(eq(effort2), any(LocalDate.class), eq(false))).thenReturn(0L);
        doNothing().when(cleanupService).destroy(eq(effort2));

        cleanupScheduler.processCleanup();

        verify(cleanupConfig).getSpots();
    }

    @Test
    void processCleanup_deleteThrowsException() {
        Map<String, Cleanup> map = new TreeMap<>();
        map.put("name1", new Cleanup());
        map.put("name2", new Cleanup());
        when(cleanupConfig.getSpots()).thenReturn(map);

        CleanupService.CleaningEffort effort1 = new CleanupService.CleaningEffort("name1",
                "countQuery", "deleteUntilQuery", "deleteUntilBatchQuery", 5,
                null, null);

        when(cleanupService.create(eq("name1"), any(Cleanup.class))).thenReturn(effort1);
        when(cleanupService.getCount(eq(effort1), any(LocalDate.class))).thenReturn(5L);
        when(cleanupService.delete(eq(effort1), any(LocalDate.class), eq(true)))
                .thenAnswer(invocation -> { throw new EOFException(null); });
        doNothing().when(cleanupService).destroy(eq(effort1));

        cleanupScheduler.processCleanup();

        verify(cleanupConfig).getSpots();
    }
}