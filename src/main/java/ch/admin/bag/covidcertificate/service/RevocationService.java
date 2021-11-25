package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.mapper.RevocationMapper;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevocationService {
    private final RevocationRepository revocationRepository;

    @Transactional
    public void createRevocation(RevocationDto revocationDto) {
        try {
            if (revocationRepository.findByUvci(revocationDto.getUvci()) != null) {
                log.info("Revocation for {} already exists.", revocationDto.getUvci());
                throw new RevocationException(DUPLICATE_UVCI);
            }
            revocationRepository.saveAndFlush(RevocationMapper.toRevocation(revocationDto));
            log.info("Revocation for {} created.", revocationDto.getUvci());
        } catch (RevocationException e) {
            throw e;
        } catch (Exception e) {
            log.error(String.format("Create revocation for %s failed.", revocationDto.getUvci()), e);
            throw e;
        }
    }

    @Transactional
    public void createListRevocation(RevocationListDto revocationListDto) {
        List<String> resultList = new LinkedList();

        for (String uvci : revocationListDto.getUvcis()) {
            try {
                if (revocationRepository.findByUvci(uvci) != null) {
                    log.info("Revocation for {} already exists.", uvci);
                    throw new RevocationException(DUPLICATE_UVCI);
                }

                RevocationDto revocationDto = new RevocationDto(uvci);
                revocationRepository.save(RevocationMapper.toRevocation(revocationDto));
                resultList.add(uvci); // what we do with the result list?
                log.info("Revocation for {} created.", uvci);
            } catch (RevocationException e) {
                throw e;
            } catch (Exception e) {
                log.error(String.format("Create revocation for %s failed.", uvci), e);
                throw e;
            }
        }
        if (resultList.size() == revocationListDto.getUvcis().size()) {
            revocationRepository.flush();
        }
    }

    @Transactional
    public void checkListRevocation(RevocationListDto revocationListDto) {
        List<String> resultList = new LinkedList();

        for (String uvci : revocationListDto.getUvcis()) {
            try {
                if (revocationRepository.findByUvci(uvci) != null) {
                    log.info("Revocation for {} already exists.", uvci);
                    throw new RevocationException(DUPLICATE_UVCI);
                }

                RevocationDto revocationDto = new RevocationDto(uvci);
                revocationRepository.save(RevocationMapper.toRevocation(revocationDto));
                resultList.add(uvci);
                log.info("Revocation for {} created.", uvci);

            } catch (RevocationException e) {
                throw e;
            } catch (Exception e) {
                log.error(String.format("Create revocation for %s failed.", uvci), e);
                throw e;
            }
        }
        if (resultList.size() == revocationListDto.getUvcis().size()) {
            revocationRepository.flush();
        }
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
