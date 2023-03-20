package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.config.cleanup.Cleanup;
import ch.admin.bag.covidcertificate.config.cleanup.CleanupConfig;
import ch.admin.bag.covidcertificate.service.CleanupService.CleaningEffort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
// run on cloudfoundry-instance 0 OR on local spring profile
@ConditionalOnExpression("'${CF_INSTANCE_INDEX}'=='0' or {'local'}.contains('${SPRING_PROFILES_ACTIVE}')")
public class CleanupScheduler {

    private final CleanupService cleanupService;
    private final CleanupConfig cleanupConfig;
    private static final int MAX_RETRIES = 3;


    public CleanupScheduler(CleanupService cleanupService, CleanupConfig cleanupConfig) {
        this.cleanupService = cleanupService;
        this.cleanupConfig = cleanupConfig;

        log.info("CLEANUP | scheduler active");
    }

    @Transactional
    @Scheduled(cron = "${cc-management-service.cleanup.cron}")
    public void processCleanup() {

        log.debug("CLEANUP | Setup");
        List<CleaningEffort> efforts = new ArrayList<>();
        Map<String, Cleanup> spots = cleanupConfig.getSpots();
        for (String databaseName : spots.keySet()) {
            CleaningEffort effort = cleanupService.create(databaseName, spots.get(databaseName));
            if (effort != null) {
                efforts.add(effort);
            }
        }

        final LocalDate toDeleteBeforeDate = LocalDate.now().minusYears(2);

        log.info("CLEANUP | Start - records older than >{}<", toDeleteBeforeDate.format(DateTimeFormatter.ISO_DATE));

        StopWatch stopWatch = new StopWatch();

        for (CleaningEffort effort : efforts) {
            int retry = 0;
            long count = cleanupService.getCount(effort, toDeleteBeforeDate);
            if (count == 0) {
                log.info("CLEANUP | {} - no records to clean", effort.getName());
            } else {
                log.debug("CLEANUP | {} - cleaning {} records", effort.getName(), count);
                stopWatch.start();
                long allDeleted = 0L;
                long deleted = -1L;
                while (deleted != 0) {
                    try {
                        deleted = cleanupService.delete(effort, toDeleteBeforeDate, true);
                        allDeleted += deleted;
                    } catch (Exception e) {
                        log.error("CLEANUP | {} - cleaning failed, retry #{}/{}; throwed '{}'", effort.getName(), ++retry, MAX_RETRIES, e);
                        if (retry < MAX_RETRIES) {
                            deleted = -1;
                        }
                    }
                }
                if (retry < MAX_RETRIES) {
                    // make sure that fraction of a batch are deleted aswell
                    deleted = cleanupService.delete(effort, toDeleteBeforeDate, false);
                    allDeleted += deleted;
                }
                stopWatch.stop();

                log.info("CLEANUP | {} - {}/{} records cleaned in {}", effort.getName(), allDeleted, count, readableMillis(stopWatch.getLastTaskTimeMillis()));
            }
        }
        log.info("CLEANUP | End");

        log.debug("CLEANUP | Tear down");
        for (CleaningEffort effort : efforts) {
            cleanupService.destroy(effort);
        }
    }

    private static final DateTimeFormatter millisFormatter = DateTimeFormatter.ofPattern("'%d'.SSS's'");

    private String readableMillis(long millis) {
        LocalDateTime dateTime = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault()) // default zone
                .toLocalDateTime();
        String format = dateTime.format(millisFormatter);
        return String.format(format, dateTime.getMinute()*60 + dateTime.getSecond());
    }
}
