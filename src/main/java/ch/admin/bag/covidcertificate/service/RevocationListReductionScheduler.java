package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
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
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_REVOCATION_LIST_REDUCTION;
import static ch.admin.bag.covidcertificate.service.KpiDataService.CRON_ACCOUNT_CC_MANAGEMENT_SERVICE;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "CF_INSTANCE_INDEX", havingValue = "0")
public class RevocationListReductionScheduler {

    private final RevocationRepository revocationRepository;
    private final KpiDataService kpiDataService;

    @Value("${cc-management-service.update-deleted-marker.batch-size}")
    private int batchSize;
    @Value("${cc-management-service.update-deleted-marker.days-protected}")
    private int daysProtected;

    @Transactional
    @Scheduled(cron = "${cc-management-service.update-deleted-marker.cron}")
    public void updateDeletedMarker() {
        final var jobDateTime = LocalDateTime.now();
        log.info("Starting reduction of revocation list, updating deleted marker at {}",
                jobDateTime.format(DateTimeFormatter.ISO_DATE_TIME));

        LocalDate deleteDay = LocalDate.now();
        LocalTime deleteTime = LocalTime.now();
        LocalDateTime deleteDate = LocalDateTime.of(deleteDay, deleteTime);
        LocalDateTime latestValidDate = LocalDateTime.of(deleteDay.minusDays(daysProtected), LocalTime.MIDNIGHT);

        List<Revocation> deletableUvcis = revocationRepository.findDeletableUvcis(latestValidDate, batchSize);
        for (Revocation revocation : deletableUvcis) {
            revocation.setDeletedDateTime(deleteDate);
            try {
                revocationRepository.save(revocation);
                kpiDataService.logRevocationListReductionKpiWithoutSecurityContext(
                        KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY,
                        KPI_TYPE_REVOCATION_LIST_REDUCTION,
                        revocation.getUvci(),
                        SystemSource.RevocationListReduction,
                        CRON_ACCOUNT_CC_MANAGEMENT_SERVICE);
            } catch (Exception ex) {
                // keep rolling but log the issue
                log.error("Exception updating deleted marker", ex);
            }
        }
        log.info("Ending update deleted marker");
    }
}
