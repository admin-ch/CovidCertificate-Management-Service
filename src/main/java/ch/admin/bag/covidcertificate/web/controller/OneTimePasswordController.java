package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CustomTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
@Slf4j
public class OneTimePasswordController {

    private final SecurityHelper securityHelper;

    private final CustomTokenProvider customTokenProvider;

    private final ServletJeapAuthorization jeapAuthorization;

    @PostMapping()
    @PreAuthorize("hasRole('bag-cc-certificatecreator')")
    public String createOneTimePassword(HttpServletRequest request) {
        log.info("Call of Create OTP");
        securityHelper.authorizeUser(request);

        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();

        String otp = customTokenProvider.createToken(token.getClaimAsString(USER_EXT_ID_CLAIM_KEY), token.getClaimAsString("homeName"));
        log.info("kpi: {} {} {}", kv(KPI_TIMESTAMP_KEY, ZonedDateTime.now(SWISS_TIMEZONE).format(LOG_FORMAT)), kv(KPI_OTP_SYSTEM_KEY, KPI_SYSTEM_UI), kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY)));
        return otp;
    }

}
