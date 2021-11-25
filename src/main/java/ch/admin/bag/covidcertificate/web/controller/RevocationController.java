package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationError;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.Revocation;
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

import static ch.admin.bag.covidcertificate.api.Constants.*;
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

    public class RevocationErrorItem {
        private  RevocationError revocationError;
        private String errorMessage = "";
        private String errorUvci = "";

        public RevocationErrorItem(String message, String uvci) {
            errorMessage = message;
            errorUvci = uvci;
            revocationError = INVALID_UVCI;
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody RevocationDto revocationDto, HttpServletRequest request) {
        log.info("Call of create revocation for uvci {}.", revocationDto.getUvci());
        securityHelper.authorizeUser(request);
        revocationDto.validate();
        revocationService.createRevocation(revocationDto);
        logKpi(revocationDto.getUvci());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/uvcilist/check")
    @PreAuthorize("hasAnyRole('bag-cc-superuser')")
    @ApiResponse(responseCode = "202", description = "CHECKED")
    public ResponseEntity<HttpStatus> check(@Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) throws Exception {
        log.info("Call of check uvci list.");
        securityHelper.authorizeUser(request);

        if (revocationListDto.getUvcis().size() > 100) {
            throw new RevocationException(INVALID_SIZE_OF_UVCI_LIST);
        }

        List<RevocationErrorItem> revocationErrorList = new LinkedList();
        List<Revocation> revocationList = new LinkedList();

        try {
            revocationListDto.validateList();
            for (String uvci : revocationListDto.getUvcis()) {
                try {
                    revocationService.checkListRevocation(revocationListDto);
                    logKpi(uvci);
                    revocationList.add(new Revocation(uvci)); // TODO what we do with revocationList
                } catch (Exception revocationException) {
                    RevocationErrorItem item = new RevocationErrorItem(revocationException.getMessage(), uvci); // maybe the RevocationErrorItem class is an overkill
                    revocationErrorList.add(item);
                    if (revocationErrorList.size() > 1) {
                        throw new Exception("The provided list has too UVCI Errors");
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception validationListException) {
            throw new RevocationException(INVALID_UVCI_LIST);
        }
    }

    @PostMapping("/uvcilist/revoke")
    @PreAuthorize("hasAnyRole('bag-cc-superuser')")
    @ApiResponse(responseCode = "202", description = "REVOKED")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody RevocationListDto revocationListDto, HttpServletRequest request) throws Exception {
        log.info("Call of revocate uvci list.");
        securityHelper.authorizeUser(request);

        List<RevocationErrorItem> revocationErrorList = new LinkedList<>();
        List<Revocation> revocationList = new LinkedList<>();

        try {
            revocationListDto.validateList();
            for (String uvci : revocationListDto.getUvcis()) {
                try {
                    revocationService.createListRevocation(revocationListDto);
                    logKpi(uvci);
                    revocationList.add(new Revocation(uvci)); // TODO what we do with revocationList
                } catch (Exception revocationException) {
                    RevocationErrorItem item = new RevocationErrorItem(revocationException.getMessage(), uvci); // maybe the RevocationErrorItem class is an overkill
                    revocationErrorList.add(item);
                    if (revocationErrorList.size() > 1) {
                        throw new Exception("The provided list has too UVCI Errors");
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception validationListException) {
            throw new RevocationException(INVALID_UVCI_LIST);
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
