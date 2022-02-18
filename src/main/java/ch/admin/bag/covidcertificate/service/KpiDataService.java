package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
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
import java.util.Optional;

import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_COUNTRY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_CREATE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_DETAILS;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_SYSTEM_UI;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_ANTIBODY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_EXCEPTIONAL;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY_RAT;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION_TOURIST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
import static ch.admin.bag.covidcertificate.api.Constants.PREFERRED_USERNAME_CLAIM_KEY;
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
                case ANTIBODY_TEST:
                    typeCodeDetailString = DETAILS_ANTIBODY;
                    break;
                case EXCEPTIONAL_TEST:
                    typeCodeDetailString = DETAILS_MEDICAL_EXCEPTION;
                    break;
            }
        } else {
            typeCodeDetailString = DETAILS_RAPID;
        }
        return typeCodeDetailString;
    }

    @Transactional
    public void logVaccinationCertificateGenerationKpi(VaccinationCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_VACCINATION, uvci,
                                    createDto.getVaccinationInfo().get(0).getMedicinalProductCode(),
                                    createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
    }

    @Transactional
    public void logVaccinationTouristCertificateGenerationKpi(
            VaccinationTouristCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_VACCINATION_TOURIST, uvci,
                                    createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode(),
                                    createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination());
    }

    @Transactional
    public void logRecoveryCertificateGenerationKpi(RecoveryCertificateCreateDto createDto, String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_RECOVERY, uvci, null, createDto.getRecoveryInfo().get(0).getCountryOfTest());
    }

    @Transactional
    public void logRecoveryRatCertificateGenerationKpi(RecoveryRatCertificateCreateDto createDto, String uvci) {
        var typeCode = TestType.findByTypeCode(createDto.getTestInfo().get(0).getTypeCode());
        logCertificateGenerationKpi(KPI_TYPE_RECOVERY_RAT,
                                    uvci,
                                    getDetails(typeCode),
                                    createDto.getTestInfo().get(0).getMemberStateOfTest());
    }

    @Transactional
    public void logAntibodyCertificateGenerationKpi(String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_ANTIBODY,
                                    uvci,
                                    DETAILS_ANTIBODY,
                                    ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    @Transactional
    public void logExceptionalCertificateGenerationKpi(String uvci) {
        logCertificateGenerationKpi(KPI_TYPE_EXCEPTIONAL,
                                    uvci,
                                    DETAILS_MEDICAL_EXCEPTION,
                                    ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
    }

    private void logCertificateGenerationKpi(String type, String uvci, String details, String country) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        if (token == null) {
            return;
        }
        final String claimString = token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY);
        if (claimString != null && !SERVICE_ACCOUNT_CC_API_GATEWAY_SERVICE.equalsIgnoreCase(claimString)) {
            // the request is from Web-UI, so we need to log it
            var kpiTimestamp = LocalDateTime.now();
            writeKpiInLog(type, details, country, kpiTimestamp, token);
            saveKpiData(new KpiData(kpiTimestamp, type, claimString, uvci, details, country, false));
        }
    }

    private void writeKpiInLog(String type, String details, String country, LocalDateTime kpiTimestamp, Jwt token){
        var timestampKVPair = kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT));
        var systemKVPair = kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, KPI_SYSTEM_UI);
        var kpiTypeKVPair =kv(KPI_TYPE_KEY, type);
        var kpiDetailsKVPair = kv(KPI_DETAILS, details);
        var kpiCountryKVPair = kv(KPI_COUNTRY, country);
        var uuidKVPair = kv(KPI_UUID_KEY, token.getClaimAsString(PREFERRED_USERNAME_CLAIM_KEY));

        if(details == null){
            log.info("kpi: {} {} {} {} {}", timestampKVPair, systemKVPair, kpiTypeKVPair, uuidKVPair, kpiCountryKVPair);
        }else {
            log.info("kpi: {} {} {} {} {} {}",timestampKVPair, systemKVPair, kpiTypeKVPair, kpiDetailsKVPair, uuidKVPair, kpiCountryKVPair);
        }
    }
}
