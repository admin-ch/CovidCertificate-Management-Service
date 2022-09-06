package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.conversion.ConversionReason;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
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

import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_CONVERSION_OLD_UVCI_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_COUNTRY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_CREATE_CERTIFICATE_SYSTEM_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_DETAILS;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TIMESTAMP_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_ANTIBODY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_CERTIFICATE_CONVERSION;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_EXCEPTIONAL;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_RECOVERY_RAT_EU;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_VACCINATION_TOURIST;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_USED_KEY_IDENTIFIER;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UUID_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.KPI_UVCI_KEY;
import static ch.admin.bag.covidcertificate.api.Constants.LOG_FORMAT;
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
    public static final String CONVERSION_USER = "conversion";

    private final KpiDataRepository logRepository;
    private final ServletJeapAuthorization jeapAuthorization;

    @Transactional
    public void saveKpiData(KpiData kpiLog) {
        logRepository.save(kpiLog);
    }

    public void logTestCertificateGenerationKpi(
            TestCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {
        var typeCode = TestType.findByTypeCode(createDto.getTestInfo().get(0).getTypeCode());
        logCertificateGenerationKpi(KPI_TYPE_TEST,
                                    uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    getDetailsForTestCertificate(typeCode),
                                    createDto.getTestInfo().get(0).getMemberStateOfTest(),
                                    usedKeyIdentifier);
    }

    private String getDetailsForTestCertificate(Optional<TestType> typeCode) {
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

    public void logVaccinationCertificateGenerationKpi(
            VaccinationCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_VACCINATION, uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    createDto.getVaccinationInfo().get(0).getMedicinalProductCode(),
                                    createDto.getVaccinationInfo().get(0).getCountryOfVaccination(),
                                    usedKeyIdentifier);
    }

    public void logVaccinationTouristCertificateGenerationKpi(
            VaccinationTouristCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_VACCINATION_TOURIST, uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode(),
                                    createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination(),
                                    usedKeyIdentifier);
    }

    public void logRecoveryCertificateGenerationKpi(
            RecoveryCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_RECOVERY,
                                    uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    null,
                                    createDto.getRecoveryInfo().get(0).getCountryOfTest(),
                                    usedKeyIdentifier);
    }

    public void logRecoveryRatCertificateGenerationKpi(
            RecoveryRatCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_RECOVERY_RAT_EU,
                                    uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    DETAILS_RAPID,
                                    createDto.getTestInfo().get(0).getMemberStateOfTest(),
                                    usedKeyIdentifier);
    }

    public void logAntibodyCertificateGenerationKpi(
            AntibodyCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_ANTIBODY,
                                    uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    DETAILS_ANTIBODY,
                                    ISO_3166_1_ALPHA_2_CODE_SWITZERLAND,
                                    usedKeyIdentifier);
    }

    public void logExceptionalCertificateGenerationKpi(
            ExceptionalCertificateCreateDto createDto,
            String uvci,
            String usedKeyIdentifier) {

        logCertificateGenerationKpi(KPI_TYPE_EXCEPTIONAL,
                                    uvci,
                                    createDto.getSystemSource(),
                                    createDto.getUserExtId(),
                                    DETAILS_MEDICAL_EXCEPTION,
                                    ISO_3166_1_ALPHA_2_CODE_SWITZERLAND,
                                    usedKeyIdentifier);
    }

    public void logCertificateConversionKpi(
            VaccinationCertificateConversionRequestDto conversionDto,
            String newUvci,
            String usedKeyIdentifier) {

        logCertificateConversionKpi(
                conversionDto.getDecodedCert()
                             .getVaccinationInfo()
                             .get(0)
                             .getCountryOfVaccination(), // existing country column
                // existing column system source
                newUvci, // existing UVCI column
                conversionDto.getDecodedCert().getVaccinationInfo().get(0).getIdentifier(), // new origin_uvci column
                usedKeyIdentifier,
                conversionDto.getConversionReason()); // new column conversion_reason
    }

    private void logCertificateGenerationKpi(
            String type,
            String uvci,
            SystemSource systemSource,
            String userExtId,
            String details,
            String country,
            String usedKeyIdentifier) {

        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);

        var kpiTimestamp = LocalDateTime.now();
        writeCertificateCreationKpiInLog(type, details, country, kpiTimestamp, systemSource, relevantUserExtId, usedKeyIdentifier);
        saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, type, relevantUserExtId, systemSource.category)
                        .withUvci(uvci)
                        .withDetails(details)
                        .withCountry(country)
                        .withKeyIdentifier(usedKeyIdentifier)
                        .build()
        );
    }

    private void logCertificateConversionKpi(
            String country,
            String newUvci,
            String oldUvci,
            String usedKeyIdentifier,
            ConversionReason conversionReason) {

        var kpiTimestamp = LocalDateTime.now();
        writeCertificateConversionKpiInLog(
                oldUvci,
                newUvci,
                country,
                kpiTimestamp,
                usedKeyIdentifier);
        saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp,
                                           KPI_TYPE_CERTIFICATE_CONVERSION,
                                           CONVERSION_USER,
                                           SystemSource.Conversion.category)
                        .withUvci(newUvci)
                        .withOriginUvci(oldUvci)
                        .withCountry(country)
                        .withKeyIdentifier(usedKeyIdentifier)
                        .withConversionReason(conversionReason.name())
                        .build()
        );
    }

    private void writeCertificateCreationKpiInLog(
            String type,
            String details,
            String country,
            LocalDateTime kpiTimestamp,
            SystemSource systemSource,
            String userExtId,
            String usedKeyIdentifier) {

        var timestampKVPair = kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT));
        var systemKVPair = kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, systemSource.category);
        var kpiTypeKVPair = kv(KPI_TYPE_KEY, type);
        var kpiDetailsKVPair = kv(KPI_DETAILS, details);
        var userIdKVPair = kv(KPI_UUID_KEY, userExtId);
        var kpiCountryKVPair = kv(KPI_COUNTRY, country);
        var kpiUsedKeyIdentifierKVPair = kv(KPI_USED_KEY_IDENTIFIER, usedKeyIdentifier);

        if (details == null) {
            log.info("kpi: {} {} {} {} {} {}",
                     timestampKVPair,
                     systemKVPair,
                     kpiTypeKVPair,
                     userIdKVPair,
                     kpiCountryKVPair,
                     kpiUsedKeyIdentifierKVPair);
        } else {
            log.info("kpi: {} {} {} {} {} {} {}",
                     timestampKVPair,
                     systemKVPair,
                     kpiTypeKVPair,
                     kpiDetailsKVPair,
                     userIdKVPair,
                     kpiCountryKVPair,
                     kpiUsedKeyIdentifierKVPair);
        }
    }

    private void writeCertificateConversionKpiInLog(
            String oldUvci,
            String newUvci,
            String country,
            LocalDateTime kpiTimestamp,
            String usedKeyIdentifier) {

        var timestampKVPair = kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT));
        var systemKVPair = kv(KPI_CREATE_CERTIFICATE_SYSTEM_KEY, SystemSource.Conversion.category);
        var kpiTypeKVPair = kv(KPI_TYPE_KEY,
                               ch.admin.bag.covidcertificate.api.Constants.KPI_TYPE_CERTIFICATE_CONVERSION);
        var kpiOldUvciKVPair = kv(KPI_CONVERSION_OLD_UVCI_KEY, oldUvci);
        var kpiNewUvciKVPair = kv(KPI_UVCI_KEY, newUvci);
        var userIdKVPair = kv(KPI_UUID_KEY, KpiDataService.CONVERSION_USER);
        var kpiCountryKVPair = kv(KPI_COUNTRY, country);
        var kpiUsedKeyIdentifierKVPair = kv(KPI_USED_KEY_IDENTIFIER, usedKeyIdentifier);

        log.info("kpi: {} {} {} {} {} {} {} {}",
                 timestampKVPair,
                 systemKVPair,
                 kpiTypeKVPair,
                 userIdKVPair,
                 kpiCountryKVPair,
                 kpiOldUvciKVPair,
                 kpiNewUvciKVPair,
                 kpiUsedKeyIdentifierKVPair);
    }

    public void logRevocationKpi(
            String systemKey, String kpiType, String uvci, SystemSource systemSource, String userExtId) {
        Jwt token = jeapAuthorization.getJeapAuthenticationToken().getToken();
        String relevantUserExtId = UserExtIdHelper.extractUserExtId(token, userExtId, systemSource);
        LocalDateTime kpiTimestamp = LocalDateTime.now();
        log.info("kpi: {} {} {} {}",
                 kv(KPI_TIMESTAMP_KEY, kpiTimestamp.format(LOG_FORMAT)),
                 kv(KPI_TYPE_KEY, kpiType),
                 kv(KPI_UUID_KEY, relevantUserExtId),
                 kv(systemKey, systemSource.category));
        saveKpiData(
                new KpiData.KpiDataBuilder(kpiTimestamp, kpiType, relevantUserExtId, systemSource.category)
                        .withUvci(uvci)
                        .build()
        );
    }
}
