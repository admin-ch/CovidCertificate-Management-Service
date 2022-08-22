package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.validator.UvciValidator;
import ch.admin.bag.covidcertificate.api.response.RevocationListResponseDto;
import ch.admin.bag.covidcertificate.api.response.RevocationResponseDto;
import ch.admin.bag.covidcertificate.api.response.RevocationStatus;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.KPI_REVOKE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_REVOCATION;

@RestController
@RequestMapping("/api/v1/revocation")
@RequiredArgsConstructor
@Slf4j
public class RevocationController {
    private final RevocationService revocationService;
    private final KpiDataService kpiDataService;

    @PostMapping
    @ApiResponse(responseCode = "201", description = "CREATED")
    public RevocationResponseDto create(@Valid @RequestBody RevocationDto revocationDto) {
        log.info("Call of create revocation.");
        final String uvci = revocationDto.getUvci();
        UvciValidator.validateUvciMatchesSpecification(revocationDto.getUvci());

        LocalDateTime revocationDateTime = revocationService.getRevocationDateTime(uvci);
        if (revocationDateTime != null) {
            return new RevocationResponseDto(
                    RevocationStatus.ALREADY_REVOKED,
                    revocationDateTime
            );
        }

        revocationService.createRevocation(revocationDto.getUvci(), revocationDto.isFraud());
        kpiDataService.logRevocationKpi(
                KPI_REVOKE_CERTIFICATE_SYSTEM_KEY,
                KPI_TYPE_REVOCATION,
                revocationDto.getUvci(),
                revocationDto.getSystemSource(),
                revocationDto.getUserExtId());
        return new RevocationResponseDto(RevocationStatus.OK);
    }

    @PostMapping("/uvcilist")
    @ApiResponse(responseCode = "201", description = "CREATED")
    public RevocationListResponseDto massRevocation(@Valid @RequestBody RevocationListDto revocationListDto) {
        log.info("Call of mass-revocation.");

        revocationListDto.validate();

        return revocationService.performMassRevocation(revocationListDto);
    }
}
