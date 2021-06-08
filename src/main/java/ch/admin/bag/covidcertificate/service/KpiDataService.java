package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiDataService {
    private final KpiDataRepository logRepository;

    @Transactional
    public void log(KpiData kpiLog) {
        logRepository.save(kpiLog);
    }
}
