package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_REVOCATION_LIST_REDUCTION;
import static ch.admin.bag.covidcertificate.service.KpiDataService.CRON_ACCOUNT_CC_MANAGEMENT_SERVICE;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevocationListReductionService {

    private final RevocationRepository revocationRepository;
    private final KpiDataService kpiDataService;

    @Transactional
    public boolean updateDeletedMarker(LocalDateTime latestValidDate, LocalDateTime deleteDate, int batchSize) {

        List<Revocation> deletableUvcis = revocationRepository.findDeletableUvcis(latestValidDate, batchSize);

        if (CollectionUtils.isEmpty(deletableUvcis)) {
            log.info("No revocations found to be marked as deleted");
            return false;
        }

        log.info("Identified {} revocations to be marked as deleted", deletableUvcis.size());
        int markedCounter = 0;
        for (Revocation revocation : deletableUvcis) {
            revocation.setDeletedDateTime(deleteDate);
            try {
                revocationRepository.save(revocation);
                markedCounter++;
                kpiDataService.logRevocationListReductionKpiWithoutSecurityContext(
                        KPI_REVOCATION_LIST_REDUCTION_SYSTEM_KEY,
                        KPI_TYPE_REVOCATION_LIST_REDUCTION,
                        revocation.getUvci(),
                        SystemSource.RevocationListReduction,
                        CRON_ACCOUNT_CC_MANAGEMENT_SERVICE);
            } catch (Exception ex) {
                // keep rolling but log the issue
                log.error("Exception updating revocations with deleted marker", ex);
            }
        }
        log.info("Ending update of {} revocations marked as deleted", markedCounter);
        return true;
    }
}
