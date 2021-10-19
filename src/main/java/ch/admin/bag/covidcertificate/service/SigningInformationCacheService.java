package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import ch.admin.bag.covidcertificate.domain.SigningInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SigningInformationCacheService {
    private final SigningInformationRepository signingInformationRepository;
    private static final String SIGNING_INFORMATION_CACHE = "signingInformationCache";

    @Cacheable(SIGNING_INFORMATION_CACHE)
    public List<SigningInformation> findSigningInformation(String certificateType, LocalDate validAt){
        log.info("Loading signing information for {} certificate", certificateType);
        return signingInformationRepository.findSigningInformation(certificateType, validAt);
    }

    @Cacheable(SIGNING_INFORMATION_CACHE)
    public SigningInformation findSigningInformation(String certificateType, String code, LocalDate validAt){
        log.info("Loading signing information for {} certificate with code {}", certificateType, code);
        return signingInformationRepository.findSigningInformation(certificateType, code, validAt);
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = SIGNING_INFORMATION_CACHE, allEntries = true)
    public void cleanSigningInformationCache() {
        log.info("Cleaning cache of signing information");
    }
}
