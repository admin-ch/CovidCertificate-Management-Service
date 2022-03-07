package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
import ch.admin.bag.covidcertificate.util.UserExtIdHelper;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_FRAUD;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_CHECK;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/revocation")
@RequiredArgsConstructor
@Slf4j
public class RevocationController {
    private final SecurityHelper securityHelper;
    private final ServletJeapAuthorization jeapAuthorization;
    private final RevocationService revocationService;
    private final KpiDataService kpiLogService;

    @PostMapping
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody RevocationDto revocationDto, HttpServletRequest request) {
        log.info("Call of create revocation.");
        final String uvci = revocationDto.getUvci();
        securityHelper.authorizeUser(request);
        revocationDto.validate();

        if (revocationService.isAlreadyRevoked(uvci)) {
            throw new RevocationException(DUPLICATE_UVCI);
        }

        revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud());
        logRevocationKpi(KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, revocationDto.getUvci(), revocationDto.getSystemSource(), revocationDto.getUserExtId(), revocationDto.isFraud());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
    // TODO VACCINECER-2086: Enable Mass-revocation
    // TODO: make sure PreAuthorize fits new authorization

    @PostMapping("/uvcilist/check")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    @ApiResponse(responseCode = "202", description = "CHECKED")
    public CheckRevocationListResponseDto checkMassRevocation(
            @Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) {
        log.info("Call of mass-revocation-check.");
        securityHelper.authorizeUser(request);

        revocationListDto.validateList();

        Map<String, String> uvcisToErrorMessage = revocationService.getUvcisWithErrorMessage(
                revocationListDto.getUvcis());

        List<String> revocableUvcis = new ArrayList<>(revocationListDto.getUvcis());
        revocableUvcis.removeAll(uvcisToErrorMessage.keySet());

        // not existing UVCIs are handled as warning, because UVCIs issued before 07.08.2021 are always unknown
        Map<String, String> notExistingUvcisToWarningMessage = revocationService.getNotExistingUvcis(revocableUvcis);

        logRevocationCheckKpi(revocationListDto.getUserExtId(), revocationListDto.getSystemSource());

        return new CheckRevocationListResponseDto(uvcisToErrorMessage, notExistingUvcisToWarningMessage, revocableUvcis);
    }

    @PostMapping("/uvcilist")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public RevocationListResponseDto massRevocation(
            @Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) {
        log.info("Call of mass-revocation.");
        securityHelper.authorizeUser(request);

        // fraud flag will be implemented for mass-revocation with VACCINECER-2045
        boolean fraud = false;

        revocationListDto.validateList();

        Map<String, String> uvcisToErrorMessage = revocationService.getUvcisWithErrorMessage(revocationListDto.getUvcis());

        List<String> revokedUvcis = new LinkedList<>();
        for (String uvci : revocationListDto.getUvcis()) {

            String errorMessage = uvcisToErrorMessage.get(uvci);
            if (errorMessage == null) {
                try {
                    revocationService.createRevocation(uvci, fraud);
                    logRevocationKpi(KPI_TYPE_MASS_REVOCATION_SUCCESS, uvci, revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), fraud);
                    revokedUvcis.add(uvci);
                } catch (Exception ex) {
                    uvcisToErrorMessage.put(uvci, "Error during revocation");
                }
            } else {
                try {
                    if (errorMessage.startsWith(ALREADY_REVOKED_UVCI.getErrorMessage())) {
                        logRevocationKpi(KPI_TYPE_MASS_REVOCATION_REDUNDANT, uvci, revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), fraud);
                    } else if (errorMessage.equals(INVALID_UVCI.getErrorMessage())) {
                        logRevocationKpi(KPI_TYPE_MASS_REVOCATION_FAILURE, uvci, revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), fraud);
                    } else {
                        log.warn("Mass-revocation failed for unknown reason: {}.", errorMessage);
                    }
                } catch (Exception ex) {
                    log.error("Mass-revocation KPI Log failed: {} {}.", ex, ex.getLocalizedMessage());
                }
            }
        }

        return new RevocationListResponseDto(uvcisToErrorMessage, revokedUvcis);
    }
    */

    private void logRevocationKpi(String kpiType, String uvci, SystemSource systemSource, String userExtId, boolean fraud) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {} {} {} {}",
                kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)),
                kv(KPI_TYPE_KEY, kpiType),
                kv(KPI_UUID_KEY, uvci),
                kv(PREFERRED_USERNAME_CLAIM_KEY, relevantUserExtId),
                kv(KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, systemSource.category),
                kv(KPI_FRAUD, fraud));
        kpiLogService.saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, kpiType, relevantUserExtId, systemSource.category)
                        .withUvci(uvci)
                        .withFraud(fraud)
                        .build()
        );
    }

    private void logRevocationCheckKpi(String userExtId, SystemSource systemSource) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {}",
                kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)),
                kv(KPI_TYPE_KEY, KPI_TYPE_MASS_REVOCATION_CHECK),
                kv(PREFERRED_USERNAME_CLAIM_KEY, relevantUserExtId));
        kpiLogService.saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, KPI_TYPE_MASS_REVOCATION_CHECK, relevantUserExtId, systemSource.category)
                        .build()
        );
    }
}
