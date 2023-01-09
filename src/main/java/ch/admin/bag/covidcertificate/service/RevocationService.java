package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.mapper.RevocationMapper;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import ch.admin.bag.covidcertificate.api.request.validator.UvciValidator;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import ch.admin.bag.covidcertificate.domain.Revocation;
import ch.admin.bag.covidcertificate.domain.RevocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.ALREADY_REVOKED_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_FRAUD_FLAG;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_MASS_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_FAILURE;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_REDUNDANT;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevocationService {

    private final KpiDataService kpiLogService;
    private final RevocationRepository revocationRepository;
    private final KpiDataRepository kpiDataRepository;

    @Transactional
    public void createRevocation(String uvci, boolean fraud) {
        this.createRevocationInternal(uvci, fraud);
    }

    private void createRevocationInternal(String uvci, boolean fraud) {
        try {
            if (revocationRepository.findByUvci(uvci) != null) {
                log.info("Revocation for {} already exists.", uvci);
                throw new RevocationException(DUPLICATE_UVCI);
            }
            revocationRepository.saveAndFlush(RevocationMapper.toRevocation(uvci, fraud, null));
            log.info("Revocation for {} and fraud {} created.", uvci, fraud);
        } catch (RevocationException e) {
            throw e;
        } catch (Exception e) {
            log.error(String.format("Create revocation for %s and fraud %s failed.", uvci, fraud), e);
            throw e;
        }
    }

    @Transactional
    public RevocationListResponseDto performMassRevocation(RevocationListDto revocationListDto) {
        Map<String, String> uvcisToErrorMessage = getUvcisWithErrorMessage(
                revocationListDto.getUvcis()
        );

        List<String> revokedUvcis = new LinkedList<>();
        for (UvciForRevocationDto uvciForRevocation : revocationListDto.getUvcis()) {

            // revoke uvci if there are no error messages
            if (uvcisToErrorMessage.get(uvciForRevocation.getUvci()) == null) {
                try {
                    createRevocationInternal(uvciForRevocation.getUvci(), uvciForRevocation.getFraud());
                    kpiLogService.logRevocationKpi(
                            KPI_REVOKE_CERTIFICATE_SYSTEM_KEY,
                            KPI_TYPE_MASS_REVOCATION_SUCCESS,
                            uvciForRevocation.getUvci(),
                            revocationListDto.getSystemSource(),
                            revocationListDto.getUserExtId());
                    revokedUvcis.add(uvciForRevocation.getUvci());
                } catch (Exception ex) {
                    log.error("Error during mass-revocation: {}.", ex.getLocalizedMessage(), ex);
                    uvcisToErrorMessage.put(uvciForRevocation.getUvci(), "Error during revocation");
                }
            }

            // log error message if we did not attempt to revoke the uvci OR had a problem during revocation
            if (uvcisToErrorMessage.get(uvciForRevocation.getUvci()) != null) {
                String errorMessage = uvcisToErrorMessage.get(uvciForRevocation.getUvci());
                try {
                    if (errorMessage.startsWith(ALREADY_REVOKED_UVCI.getErrorMessage())) {
                        kpiLogService.logRevocationKpi(
                                KPI_MASS_REVOKE_CERTIFICATE_SYSTEM_KEY,
                                KPI_TYPE_MASS_REVOCATION_REDUNDANT,
                                uvciForRevocation.getUvci(),
                                revocationListDto.getSystemSource(),
                                revocationListDto.getUserExtId());
                    } else {
                        kpiLogService.logRevocationKpi(
                                KPI_MASS_REVOKE_CERTIFICATE_SYSTEM_KEY,
                                KPI_TYPE_MASS_REVOCATION_FAILURE,
                                uvciForRevocation.getUvci(),
                                revocationListDto.getSystemSource(),
                                revocationListDto.getUserExtId());
                    }
                } catch (Exception ex) {
                    log.error("Mass-revocation KPI Log failed: {}.", ex.getLocalizedMessage(), ex);
                }
            }
        }

        return new RevocationListResponseDto(uvcisToErrorMessage, revokedUvcis);
    }

    @Transactional(readOnly = true)
    public Map<String, String> getUvcisWithErrorMessage(List<UvciForRevocationDto> uvciForRevocationDtos) {
        List<String> uvcis = uvciForRevocationDtos.stream()
                .map(UvciForRevocationDto::getUvci)
                .toList();

        return Stream.of(
                        // get all possible errors for uvcis
                        getInvalidUvcis(uvcis).entrySet(),
                        getUvcisWithMissingFraudFlag(uvciForRevocationDtos).entrySet(),
                        getAlreadyRevokedUvcis(uvcis).entrySet()
                )
                .flatMap(Set::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        // combine messages if there are multiple errors for same uvci
                        (left, right) -> {
                            left = left + " " + right;
                            return left;
                        }
                ));
    }

    private Map<String, String> getInvalidUvcis(List<String> uvciList) {
        Map<String, String> invalidUvcisToErrorMessage = new HashMap<>();

        for (String uvci : uvciList) {
            if (!UvciValidator.isValid(uvci)) {
                invalidUvcisToErrorMessage.put(uvci, INVALID_UVCI.getErrorMessage());
            }
        }

        return invalidUvcisToErrorMessage;
    }

    private Map<String, String> getUvcisWithMissingFraudFlag(List<UvciForRevocationDto> uvciForRevocationDtos) {
        Map<String, String> invalidUvcisToErrorMessage = new HashMap<>();

        for (UvciForRevocationDto dto : uvciForRevocationDtos) {
            if (dto.getFraud() == null) {
                invalidUvcisToErrorMessage.put(dto.getUvci(), INVALID_FRAUD_FLAG.getErrorMessage());
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
                alreadyRevokedUvciToErrorMessage.put(
                        uvci,
                        ALREADY_REVOKED_UVCI.getErrorMessage() +
                                " Revocation date: " +
                                revocation.getCreationDateTime());
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
    public LocalDateTime getRevocationDateTime(String uvci) {
        Revocation revocation = revocationRepository.findByUvci(uvci);
        if (revocation != null) {
            log.info("Revocation for {} already exists.", uvci);
            return revocation.getCreationDateTime();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<String> getRevocations() {
        try {
            return revocationRepository.findNotDeletedUvcis();
        } catch (Exception e) {
            log.error("Get revocations failed.", e);
            throw e;
        }
    }
}
