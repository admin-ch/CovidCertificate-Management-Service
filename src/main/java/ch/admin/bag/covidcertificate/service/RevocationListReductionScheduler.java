package ch.admin.bag.covidcertificate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "CF_INSTANCE_INDEX", havingValue = "0")
public class RevocationListReductionScheduler {

    private final RevocationListReductionService revocationListReductionService;

    @Value("${cc-management-service.update-deleted-marker.batch-size}")
    private int batchSize;
    @Value("${cc-management-service.update-deleted-marker.days-protected}")
    private int daysProtected;

    /**
     * Method scheduled with Shedlock to detect revocations to be taken from the list of all revocations as the
     * underlying covid certificates are no longer valid.
     * To do so those revocations are marked as deleted. The covid certificate still is shown as invalid as the
     * valid to date is in the past.
     * Important is, that we don't touch revocations that got marked as fraud as those need to be 100% revoked
     * and not only invalid.
     */
    @Scheduled(cron = "${cc-management-service.update-deleted-marker.cron}")
    @Transactional
    public void detectRevocationsToBeMarkedAsDeleted() {
        final var jobDateTime = LocalDateTime.now();
        log.info("Starting reduction of list with revocations at {}",
                jobDateTime.format(DateTimeFormatter.ISO_DATE_TIME));

        LocalDate deleteDay = LocalDate.now();
        LocalTime deleteTime = LocalTime.now();
        LocalDateTime deleteDate = LocalDateTime.of(deleteDay, deleteTime);
        LocalDateTime latestValidDate = LocalDateTime.of(deleteDay.minusDays(daysProtected), LocalTime.MIDNIGHT);
        for (int repeat = 0; repeat < 10; repeat++) {
            log.info("Portion {} of {} each revocations to be marked as deleted", repeat, batchSize);
            boolean toBeContinued = this.revocationListReductionService.updateDeletedMarker(
                    latestValidDate, deleteDate, batchSize);
            if(toBeContinued == false) {
                break;
            }
        }
    }
}
