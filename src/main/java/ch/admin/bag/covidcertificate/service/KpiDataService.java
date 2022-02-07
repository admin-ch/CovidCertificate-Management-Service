package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.KpiData;
import ch.admin.bag.covidcertificate.domain.KpiDataRepository;
import ch.admin.bag.covidcertificate.util.UserExtIdHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
@Slf4j
public class KpiDataService {
    public static final String SERVICE_ACCOUNT_CC_API_GATEWAY_SERVICE = "service-account-cc-api-gateway-service";
    public static final String DETAILS_RAPID = "rapid";
    public static final String DETAILS_MEDICAL_EXCEPTION = "medical exception";
    public static final String DETAILS_ANTIBODY = "antibody";
    public static final String DETAILS_PCR = "pcr";

    private final KpiDataRepository logRepository;
    private final ServletJeapAuthorization jeapAuthorization;

    @Transactional
    public void saveKpiData(KpiData kpiLog) {
        logRepository.save(kpiLog);
    }

    @Transactional
    public void logTestCertificateGenerationKpi(TestCertificateCreateDto createDto, String uvci) {
        var typeCode = TestType.findByTypeCode(createDto.getTestInfo().get(0).getTypeCode());
        logCertificateGenerationKpi(KPI_TYPE_TEST,
                uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                getDetails(typeCode),
                createDto.getTestInfo().get(0).getMemberStateOfTest());
    }

    private String getDetails(Optional<TestType> typeCode) {
        String typeCodeDetailString = null;
        if (typeCode.isPresent()) {
            TestType foundTestType = typeCode.get();
            switch (foundTestType) {
                case PCR:
                    typeCodeDetailString = DETAILS_PCR;
                    break;
                case RAPID_TEST:
                    typeCodeDetailString = DETAILS_RAPID;
                    break;
            }
        }
        return typeCodeDetailString;
    }

    @Transactional
    public void logVaccinationCertificateGenerationKpi(VaccinationCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_VACCINATION, uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                createDto.getVaccinationInfo().get(0).getMedicinalProductCode(),
                createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
    }

    @Transactional
    public void logVaccinationTouristCertificateGenerationKpi(
            VaccinationTouristCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_VACCINATION_TOURIST, uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode(),
                createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination());
    }

    @Transactional
    public void logRecoveryCertificateGenerationKpi(RecoveryCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_RECOVERY, uvci, createDto.getSystemSource(), createDto.getUserExtId(), null, createDto.getRecoveryInfo().get(0).getCountryOfTest());
    }

    @Transactional
    public void logRecoveryRatCertificateGenerationKpi(RecoveryRatCertificateCreateDto createDto, String uvci) {
        var typeCode = TestType.findByTypeCode(createDto.getTestInfo().get(0).getTypeCode());
        logCertificateGenerationKpi(KPI_TYPE_RECOVERY_RAT,
                uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                getDetails(typeCode),
                createDto.getTestInfo().get(0).getMemberStateOfTest());
    }

    @Transactional
    public void logAntibodyCertificateGenerationKpi(AntibodyCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_ANTIBODY,
                uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                DETAILS_ANTIBODY,
                ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    @Transactional
    public void logExceptionalCertificateGenerationKpi(ExceptionalCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_EXCEPTIONAL,
                uvci,
                createDto.getSystemSource(),
                createDto.getUserExtId(),
                DETAILS_MEDICAL_EXCEPTION,
                ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    private void logCertificateGenerationKpi(String type, String uvci, SystemSource systemSource, String userExtId, String details, String country) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);

        var kpiTimestamp = LocalDateTime.now();
        writeKpiInLog(type, details, country, kpiTimestamp, systemSource, relevantUserExtId);
        saveKpiData(new KpiData(kpiTimestamp, type, relevantUserExtId, uvci, details, country, systemSource.category));
    }


    private void writeKpiInLog(String type, String details, String country, LocalDateTime kpiTimestamp, SystemSource systemSource, String userExtId) {
        var timestampKVPair = kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT));
        var systemKVPair = kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, systemSource.category);
        var kpiTypeKVPair = kv(KPI_TYPE_KEY, type);
        var kpiDetailsKVPair = kv(KPI_DETAILS, details);
        var userIdKVPair = kv(KPI_UUID_KEY, userExtId);
        var kpiCountryKVPair = kv(KPI_COUNTRY, country);

        if (details == null) {
            log.info("kpi: {} {} {} {} {}", timestampKVPair, systemKVPair, kpiTypeKVPair, userIdKVPair, kpiCountryKVPair);
        } else {
            log.info("kpi: {} {} {} {} {} {}", timestampKVPair, systemKVPair, kpiTypeKVPair, kpiDetailsKVPair, userIdKVPair, kpiCountryKVPair);
        }
    }
}
