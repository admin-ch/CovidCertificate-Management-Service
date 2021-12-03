package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.mapper.RevocationMapper;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevocationService {

    private final RevocationRepository revocationRepository;
    private final KpiDataRepository kpiDataRepository;

    @Transactional
    public void createRevocation(String uvci) {
        revocationRepository.save(RevocationMapper.toRevocation(uvci));
        log.info("Revocation for {} created.", uvci);
    }

    @Transactional(readOnly = true)
    public boolean doesUvciExist(String uvci) {
        if (kpiDataRepository.findByUvci(uvci) == null) {
            log.info("The given UVCI got not issued by the swiss system.", uvci);
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public boolean isAlreadyRevoked(String uvci) {
        if (revocationRepository.findByUvci(uvci) != null) {
            log.info("Revocation for {} already exists.", uvci);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<String> getRevocations() {
        try {
            return revocationRepository.findAllUvcis();
        } catch (Exception e) {
            log.error("Get revocations failed.", e);
            throw e;
        }
    }
}
