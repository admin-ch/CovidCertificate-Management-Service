package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.mapper.RevocationMapper;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.ALREADY_REVOKED_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevocationService {

    private final RevocationRepository revocationRepository;
    private final KpiDataRepository kpiDataRepository;

    @Transactional
    public void createRevocation(String uvci, boolean fraud) {
        try {
            if (revocationRepository.findByUvci(uvci) != null) {
                log.info("Revocation for {} already exists.", uvci);
                throw new RevocationException(DUPLICATE_UVCI);
            }
            revocationRepository.saveAndFlush(RevocationMapper.toRevocation(uvci, fraud));
            log.info("Revocation for {} created.", uvci);
        } catch (RevocationException e) {
            throw e;
        } catch (Exception e) {
            log.error(String.format("Create revocation for %s failed.", uvci), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Map<String, String> getUvcisWithErrorMessage(List<String> uvciList) {
        Map<String, String> uvcisToErrorMessage = Stream
                .concat(
                        getInvalidUvcis(uvciList).entrySet().stream(),
                        getAlreadyRevokedUvcis(uvciList).entrySet().stream()
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return uvcisToErrorMessage;
    }


    @Transactional(readOnly = true)
    public Map<String, String> getInvalidUvcis(List<String> uvciList) {
        Map<String, String> invalidUvcisToErrorMessage = new HashMap<>();

        for (String uvci : uvciList) {
            if (!UVCI.isValid(uvci)) {
                invalidUvcisToErrorMessage.put(uvci, INVALID_UVCI.getErrorMessage());
            }
        }

        return invalidUvcisToErrorMessage;
    }

    @Transactional(readOnly = true)
    public Map<String, String> getAlreadyRevokedUvcis(List<String> uvciList) {
        Map<String, String> alreadyRevokedUvciToErrorMessage = new HashMap<>();

        for (String uvci : uvciList) {
            Revocation revocation = revocationRepository.findByUvci(uvci);
            if (revocation != null) {
                alreadyRevokedUvciToErrorMessage.put(uvci, ALREADY_REVOKED_UVCI.getErrorMessage() + " Revocation date: " + revocation.getCreationDateTime());
            }
        }

        return alreadyRevokedUvciToErrorMessage;
    }

    @Transactional(readOnly = true)
    public boolean doesUvciExist(String uvci) {
        if (kpiDataRepository.findByUvci(uvci) == null) {
            log.info("The given UVCI got not issued by the swiss system.");
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
