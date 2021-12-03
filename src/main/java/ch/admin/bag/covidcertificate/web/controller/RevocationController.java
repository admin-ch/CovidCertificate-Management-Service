package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationError;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.response.CheckRevocationListResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
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
import java.util.LinkedList;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.ERROR_SAVING_REVOCATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SIZE_OF_UVCI_LIST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_SYSTEM_UI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.NON_EXISTING_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.USER_EXT_ID_CLAIM_KEY;
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
        final String uvci = revocationDto.getUvci();
        log.info("Call of create revocation for uvci {}.", uvci);
        securityHelper.authorizeUser(request);
        revocationDto.validate();
        if (!revocationService.doesUvciExist(uvci)) {
            throw new RevocationException(NON_EXISTING_UVCI);
        }
        if (revocationService.isAlreadyRevoked(uvci)) {
            throw new RevocationException(DUPLICATE_UVCI);
        }
        revocationService.createRevocation(revocationDto.getUvci());
        logKpi(revocationDto.getUvci());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/uvcilist/check")
    @PreAuthorize("hasAnyRole('bag-cc-superuser')")
    @ApiResponse(responseCode = "202", description = "CHECKED")
    public CheckRevocationListResponseDto check(
            @Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) {
        log.info("Call of check uvci list with {}.", revocationListDto.getUvcis());
        securityHelper.authorizeUser(request);

        if (revocationListDto.getUvcis().size() > 100) {
            throw new RevocationException(INVALID_SIZE_OF_UVCI_LIST);
        }
        revocationListDto.validateList();

        List<String> revocationList = new LinkedList<>();
        String failingUvci = null;
        RevocationError error = null;
        for (String uvci : revocationListDto.getUvcis()) {
            if (!revocationService.doesUvciExist(uvci)) {
                failingUvci = uvci;
                error = NON_EXISTING_UVCI;
                break;
            }
            if (revocationService.isAlreadyRevoked(uvci)) {
                failingUvci = uvci;
                error = DUPLICATE_UVCI;
                break;
            }
            try {
                revocationService.createRevocation(uvci);
                logKpi(uvci);
                revocationList.add(uvci);
            } catch (Exception ex) {
                log.error(String.format("Create revocation for %s failed.", uvci), ex);
                failingUvci = uvci;
                error = ERROR_SAVING_REVOCATION;
                break;
            }
        }
        if (error != null) {
            return new CheckRevocationListResponseDto(error, revocationList, failingUvci);
        } else {
            return new CheckRevocationListResponseDto(null, revocationList, null);
        }
    }

    @PostMapping("/uvcilist/revoke")
    @PreAuthorize("hasAnyRole('bag-cc-superuser')")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public CheckRevocationListResponseDto create(
            @Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) throws Exception {
        log.info("Call of revoke uvci list with {}.", revocationListDto.getUvcis());
        securityHelper.authorizeUser(request);

        if (revocationListDto.getUvcis().size() > 100) {
            throw new RevocationException(INVALID_SIZE_OF_UVCI_LIST);
        }
        revocationListDto.validateList();

        List<String> revocationList = new LinkedList<>();
        String failingUvci = null;
        RevocationError error = null;
        for (String uvci : revocationListDto.getUvcis()) {
            if (!revocationService.doesUvciExist(uvci)) {
                failingUvci = uvci;
                error = NON_EXISTING_UVCI;
                break;
            }
            if (revocationService.isAlreadyRevoked(uvci)) {
                failingUvci = uvci;
                error = DUPLICATE_UVCI;
                break;
            }
            revocationList.add(uvci);
        }
        if (error != null) {
            return new CheckRevocationListResponseDto(error, revocationList, failingUvci);
        } else {
            return new CheckRevocationListResponseDto(null, revocationList, null);
        }
    }

    private void logKpi(String uvci) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null && token.getClaimAsString(USER_EXT_ID_CLAIM_KEY) != null) {
            LocalDateTime kpiTimestamp = LocalDateTime.now();
            log.info("kpi: {} {} {}", kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)), kv(KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
            kpiLogService.saveKpiData(new KpiData(kpiTimestamp, KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY), uvci, null, null));
        }
    }
}
