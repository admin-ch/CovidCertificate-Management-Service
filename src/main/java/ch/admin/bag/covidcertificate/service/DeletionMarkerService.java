package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeletionMarkerService {

    private final RevocationRepository revocationRepository;
//    private final KpiDataService kpiDataService;

    @Value("${cc-management-service.update-deleted-marker.batch-size}")
    private int batchSize;
    @Value("${cc-management-service.update-deleted-marker.days-protected}")
    private int daysProtected;

    @Transactional
    @Scheduled(cron = "${cc-management-service.update-deleted-marker.cron}")
    @SchedulerLock( name = "updateDeletedMarker", lockAtLeastFor = "2h", lockAtMostFor = "3h")
    public void updateDeletedMarker() {

        // make sure this is uniquely processed
        LockAssert.assertLocked();

        log.info("Starting update deleted marker");

        LocalDateTime deleteDate = LocalDateTime.now();
        LocalDateTime latestValidDate = deleteDate.minusDays(daysProtected);

        List<Revocation> deletableUvcis = revocationRepository.findDeletableUvcis(latestValidDate, batchSize);
        for (Revocation revocation : deletableUvcis) {
            revocation.setDeletedDateTime(deleteDate);
            try {
                revocationRepository.save(revocation);
//                kpiDataService.logRevocationKpi();
            } catch (Exception e) {
                // keep rolling
            }
        }
        log.info("Ending update deleted marker");
    }
}
