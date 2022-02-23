package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.service.CustomTokenProvider;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.KPI_OTP_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_SYSTEM_UI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Slf4j
public class OneTimePasswordController {

    private final SecurityHelper securityHelper;

    private final CustomTokenProvider customTokenProvider;

    private final ServletJeapAuthorization jeapAuthorization;

    private final KpiDataService kpiLogService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('bag-cc-certificatecreator', 'bag-cc-superuser')")
    public String createOneTimePassword(HttpServletRequest request) {
        log.info("Call of Create OTP");
        securityHelper.authorizeUser(request);

        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();

        String otp = customTokenProvider.createToken(token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY), token.getClaimAsString("homeName"));
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {}", kv(KPI_TIMESTAMP_KEY, ZonedDateTime.now(SWISS_TIMEZONE).format(LOG_FORMAT)), kv(KPI_OTP_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_UUID_KEY, token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY)));
        kpiLogService.saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, KPI_OTP_SYSTEM_KEY, token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY), SystemSource.WebUI.category)
                        .build()
        );
        return otp;
    }

}
