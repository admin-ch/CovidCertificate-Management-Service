package ch.admin.bag.covidcertificate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "CF_INSTANCE_INDEX", havingValue = "0")
public class VaccineImportScheduler {

    private final VaccineImportService vaccineImportService;

    @Scheduled(cron = "${cc-management-service.vaccine-value-set-import.cron}")
    @Transactional
    public void processVaccineValueSetUpdate() {
        final var jobDateTime = LocalDateTime.now();
        log.info("Starting check of new vaccine value sets at {}",
                jobDateTime.format(DateTimeFormatter.ISO_DATE_TIME));

        LocalDate importDate = LocalDate.now();
        boolean areItemsProcessed = vaccineImportService.updateValueSetOfVaccines(importDate);
        if(areItemsProcessed) {
            log.info("Value set of vaccines got processed. Check the value_set_update_log for identified manual tasks");
        } else {
            log.info("No processing needed for value set of vaccines.");
        }
        log.info("Ending nightly check of vaccine value sets");
    }
}
