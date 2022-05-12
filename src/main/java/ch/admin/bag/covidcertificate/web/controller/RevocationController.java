package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import ch.admin.bag.covidcertificate.api.request.validator.UvciValidator;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ALREADY_REVOKED_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_FRAUD;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_FAILURE;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_REDUNDANT;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_MASS_REVOCATION_SUCCESS;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/revocation")
@RequiredArgsConstructor
@Slf4j
public class RevocationController {
    private final ServletJeapAuthorization jeapAuthorization;
    private final RevocationService revocationService;
    private final KpiDataService kpiLogService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "CREATED")
    public ResponseEntity<HttpStatus> create(@Valid @RequestBody RevocationDto revocationDto) {
        log.info("Call of create revocation.");
        final String uvci = revocationDto.getUvci();
        UvciValidator.validateUvciMatchesSpecification(revocationDto.getUvci());

        if (revocationService.isAlreadyRevoked(uvci)) {
            throw new RevocationException(DUPLICATE_UVCI);
        }

        revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud());
        logRevocationKpi(KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, revocationDto.getUvci(), revocationDto.getSystemSource(), revocationDto.getUserExtId(), revocationDto.isFraud());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/uvcilist")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public RevocationListResponseDto massRevocation(@Valid @RequestBody RevocationListDto revocationListDto) {
        log.info("Call of mass-revocation.");

        revocationListDto.validateList();

        Map<String, String> uvcisToErrorMessage = revocationService.getUvcisWithErrorMessage(
                revocationListDto.getUvcis().stream()
                .map(UvciForRevocationDto::getUvci)
                        .collect(Collectors.toList())
        );

        List<String> revokedUvcis = new LinkedList<>();
        for (UvciForRevocationDto uvciForRevocation : revocationListDto.getUvcis()) {

            String errorMessage = uvcisToErrorMessage.get(uvciForRevocation.getUvci());
            if (errorMessage == null) {
                try {
                    revocationService.createRevocation(uvciForRevocation.getUvci(), uvciForRevocation.getFraud());
                    logRevocationKpi(KPI_TYPE_MASS_REVOCATION_SUCCESS, uvciForRevocation.getUvci(), revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), uvciForRevocation.getFraud());
                    revokedUvcis.add(uvciForRevocation.getUvci());
                } catch (Exception ex) {
                    uvcisToErrorMessage.put(uvciForRevocation.getUvci(), "Error during revocation");
                }
            } else {
                try {
                    if (errorMessage.startsWith(ALREADY_REVOKED_UVCI.getErrorMessage())) {
                        logRevocationKpi(KPI_TYPE_MASS_REVOCATION_REDUNDANT, uvciForRevocation.getUvci(), revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), uvciForRevocation.getFraud());
                    } else if (errorMessage.equals(INVALID_UVCI.getErrorMessage())) {
                        logRevocationKpi(KPI_TYPE_MASS_REVOCATION_FAILURE, uvciForRevocation.getUvci(), revocationListDto.getSystemSource(), revocationListDto.getUserExtId(), uvciForRevocation.getFraud());
                    } else {
                        log.warn("Mass-revocation failed for unknown reason: {}.", errorMessage);
                    }
                } catch (Exception ex) {
                    log.error("Mass-revocation KPI Log failed: {}.", ex.getLocalizedMessage(), ex);
                }
            }
        }

        return new RevocationListResponseDto(uvcisToErrorMessage, revokedUvcis);
    }

    private void logRevocationKpi(String kpiType, String uvci, SystemSource systemSource, String userExtId, boolean fraud) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {} {} {}",
                kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)),
                kv(KPI_TYPE_KEY, kpiType),
                kv(KPI_UUID_KEY, relevantUserExtId),
                kv(KPI_REVOKE_CERTIFICATE_SYSTEM_KEY, systemSource.category),
                kv(KPI_FRAUD, fraud));
        kpiLogService.saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, kpiType, relevantUserExtId, systemSource.category)
                        .withUvci(uvci)
                        .withFraud(fraud)
                        .build()
        );
    }
}
