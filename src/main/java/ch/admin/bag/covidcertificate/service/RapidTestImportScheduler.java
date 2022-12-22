package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.valuesets.ValueSetsClient;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
@ConditionalOnProperty(value = "CF_INSTANCE_INDEX", havingValue = "0")
public class RapidTestImportScheduler {

    private final ValueSetsClient valueSetsClient;

    private final RapidTestRepository repository;

    @Transactional
    @Scheduled(cron = "${cc-management-service.rapid-test-import.cron}")
    public void importRapidTests() {
        final var jobDateTime = LocalDateTime.now();
        log.info("Start Importing rapid tests at {}", jobDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        Map<String, ValueSetDto> rapidTests = valueSetsClient.getValueSets("covid-19-lab-test-manufacturer-and-name");
        log.info("Import {} rapid tests", rapidTests.size());
        rapidTests.forEach((k,v) -> importRapidTest(k, v, jobDateTime));

        List<RapidTest> obsoleteTests = repository.findAllByActiveAndModifiedAtIsNot(true, jobDateTime);
        log.info("Found {} entries to deactivate", obsoleteTests.size());
        obsoleteTests.forEach(v ->
        {
            log.debug("deactivate rapid test with code {}", v.getCode());
            v.deactivate(jobDateTime);
        });

        log.info("End Importing rapid tests");
    }

    private void importRapidTest(String code, ValueSetDto valueSet, LocalDateTime jobDateTime) {
        Optional<RapidTest> byCode = repository.findByCode(code);

        if (byCode.isPresent()){
            log.debug("Updating {} with {}", byCode.get(), valueSet);
            byCode.get().update(valueSet.getDisplay(), valueSet.isActive(), jobDateTime, valueSet.getValidUntil());
        } else {
            var rapidTest = repository.saveAndFlush(new RapidTest(code, valueSet.getDisplay(), valueSet.isActive(), jobDateTime, valueSet.getValidUntil()));
            log.debug("Saved {}", rapidTest);
        }
    }
}
