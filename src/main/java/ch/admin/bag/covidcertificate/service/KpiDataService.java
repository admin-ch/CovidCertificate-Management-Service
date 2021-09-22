package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.Constants.USER_EXT_ID_CLAIM_KEY;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiDataService {
    private final KpiDataRepository logRepository;
    private final ServletJeapAuthorization jeapAuthorization;

    @Transactional
    public void saveKpiData(KpiData kpiLog) {
        logRepository.save(kpiLog);
    }

    @Transactional
    public void logTestCertificateGenerationKpi(TestCertificateCreateDto createDto, String uvci) {
        var typeCode = Arrays.stream(TestType.values())
                .filter(testType -> Objects.equals(testType.typeCode, createDto.getTestInfo().get(0).getTypeCode()))
                .findFirst();
        String typeCodeDetailString = null;
        if (typeCode.isPresent() && typeCode.get().equals(TestType.PCR)) {
            typeCodeDetailString = "pcr";
        } else if (typeCode.isPresent() && typeCode.get().equals(TestType.RAPID_TEST)) {
            typeCodeDetailString = "rapid";
        }
        logCertificateGenerationKpi(KPI_TYPE_TEST, uvci, typeCodeDetailString);
    }

    @Transactional
    public void logCertificateGenerationKpi(String type, String uvci) {
        logCertificateGenerationKpi(type, uvci, null);
    }

    @Transactional
    public void logCertificateGenerationKpi(String type, String uvci, String details) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token != null && token.getClaimAsString(USER_EXT_ID_CLAIM_KEY) != null) {
            var kpiTimestamp = LocalDateTime.now();
            writeKpiInLog(type, details, kpiTimestamp, token);
            saveKpiData(new KpiData(kpiTimestamp, type, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY), uvci, details));
        }
    }

    private void writeKpiInLog(String type, String details, LocalDateTime kpiTimestamp, Jwt token){
        var timestampKVPair = kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT));
        var systemKVPair = kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI);
        var kpiTypeKVPair =kv(KPI_TYPE_KEY, type);
        var kpiDetailsKVPair = kv(KPI_DETAILS, details);
        var uuidKVPair = kv(KPI_UUID_KEY, token.getClaimAsString(USER_EXT_ID_CLAIM_KEY));

        if(details == null){
            log.info("kpi: {} {} {} {}", timestampKVPair, systemKVPair, kpiTypeKVPair, uuidKVPair);
        }else {
            log.info("kpi: {} {} {} {} {}",timestampKVPair, systemKVPair, kpiTypeKVPair, kpiDetailsKVPair, uuidKVPair);
        }
    }
}
