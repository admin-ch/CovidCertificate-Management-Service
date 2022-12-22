package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.ValueSetException;
import ch.admin.bag.covidcertificate.api.mapper.IssuableRapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.IssuableVaccineMapper;
import ch.admin.bag.covidcertificate.api.mapper.RapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccineMapper;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TYP_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.UNSUPPORTED_LANGUAGE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.DE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.EN;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.FR;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.IT;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.RM;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueSetsService {

    private static final String VALUE_SETS_CACHE_NAME = "valueSets";
    private static final String EXTENDED_VALUE_SETS_CACHE_NAME = "valueSetsExtended";
    private static final String ISSUABLE_VACCINE_DTO_CACHE_NAME = "issuableVaccineDto";
    private static final String ISSUABLE_TEST_DTO_CACHE_NAME = "issuableTestDto";
    private static final String VACCINE_CACHE_NAME = "vaccines";
    private static final String ISSUABLE_VACCINE_NAME = "issuableVaccines";
    private static final String API_GATEWAY_ISSUABLE_VACCINE_CACHE_NAME = "apiGatewayIssuableVaccines";
    private static final String WEB_UI_ISSUABLE_VACCINE_CACHE_NAME = "webUiIssuableVaccines";
    private static final String API_PLATFORM_ISSUABLE_VACCINE_CACHE_NAME = "apiPlatformIssuableVaccines";
    private static final String RAPID_TEST_CACHE_NAME = "rapidTests";
    private static final String ISSUABLE_TEST_CACHE_NAME = "issuableTests";
    private static final String COUNTRY_CODES_CACHE_NAME = "countryCodes";
    private static final String COUNTRY_CODES_FOR_LANGUAGE_CACHE_NAME = "countryCodesForLanguage";

    private final CountryCodesLoader countryCodesLoader;
    private final VaccineRepository vaccineRepository;
    private final RapidTestRepository rapidTestRepository;

    @Cacheable(VALUE_SETS_CACHE_NAME)
    public ValueSetsDto getValueSets() {
        var countryCodes = countryCodesLoader.getCountryCodes();
        return new ValueSetsDto(countryCodes, this.getWebUiIssuableVaccines(), this.getIssuableRapidTests());
    }

    @Cacheable(EXTENDED_VALUE_SETS_CACHE_NAME)
    public ValueSetsDto getExtendedValueSets() {
        var countryCodes = countryCodesLoader.getCountryCodes();
        return new ValueSetsDto(countryCodes, this.getIssuableVaccines(), this.getIssuableRapidTests());
    }

    @Cacheable(ISSUABLE_VACCINE_DTO_CACHE_NAME)
    public IssuableVaccineDto getVaccinationValueSet(String productCode) {
        var vaccinationValueSet = this.getExtendedValueSets()
                .getVaccinationSets()
                .stream()
                .filter(valueSet -> valueSet.getProductCode().equalsIgnoreCase(productCode))
                .findFirst()
                .orElse(null);
        if (vaccinationValueSet == null) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return vaccinationValueSet;
    }

    @Cacheable(ISSUABLE_TEST_DTO_CACHE_NAME)
    public IssuableTestDto validateAndGetIssuableTestDto(String testTypeCode, String testCode) {
        return validateAndGetIssuableTestDto(this.getIssuableRapidTests(), testTypeCode, testCode);
    }

    @Cacheable(COUNTRY_CODES_CACHE_NAME)
    public CountryCodes getCountryCodes() {
        log.info("Loading country codes");
        return countryCodesLoader.getCountryCodes();
    }

    private IssuableTestDto validateAndGetIssuableTestDto(Collection<IssuableTestDto> testValueSets, String testTypeCode, String testCode) {
        if (validPCRTest(testTypeCode, testCode)) {
            return new IssuableTestDto("", "PCR", TestType.PCR, null);
        } else if (validRapidTest(testTypeCode, testCode)) {
            var testValueSet = testValueSets
                    .stream()
                    .filter(issuableTestDto -> (issuableTestDto.getCode().equals(testCode)))
                    .findFirst()
                    .orElse(null);

            if (testValueSet != null) {
                return testValueSet;
            }
        }
        throw new CreateCertificateException(INVALID_TYP_OF_TEST);
    }

    private boolean validPCRTest(String testTypeCode, String testCode) {
        return Objects.equals(testTypeCode, TestType.PCR.typeCode) && !StringUtils.hasText(testCode);
    }

    private boolean validRapidTest(String testTypeCode, String testCode) {
        return (Objects.equals(testTypeCode, TestType.RAPID_TEST.typeCode)
                || !StringUtils.hasText(testTypeCode))
                && StringUtils.hasText(testCode);
    }

    public CountryCode getCountryCodeEn(String countryShort) {
        return getCountryCode(countryShort, EN);
    }

    public CountryCode getCountryCode(String countryShort, String language) {
        List<CountryCode> countryCodes = getCountryCodesForLanguage(language);
        return countryCodes.stream()
                .filter(code -> code.getShortName().equals(countryShort))
                .findFirst()
                .orElse(null);
    }

    @Cacheable(COUNTRY_CODES_FOR_LANGUAGE_CACHE_NAME)
    public List<CountryCode> getCountryCodesForLanguage(String language) {
        log.info("Loading country codes for language");
        var countryCodes = countryCodesLoader.getCountryCodes();
        List<CountryCode> result = switch (language.toLowerCase()) {
            case DE -> countryCodes.getDe();
            case IT -> countryCodes.getIt();
            case FR -> countryCodes.getFr();
            case RM -> countryCodes.getRm();
            case EN -> countryCodes.getEn();
            default -> throw new ValueSetException(UNSUPPORTED_LANGUAGE);
        };
        return result;
    }

    @Cacheable(RAPID_TEST_CACHE_NAME)
    public List<TestDto> getRapidTests() {
        log.info("Loading rapid tests");
        List<RapidTest> rapidTests = this.rapidTestRepository.findAll();
        return RapidTestMapper.fromRapidTests(rapidTests);
    }

    @Cacheable(ISSUABLE_TEST_CACHE_NAME)
    public List<IssuableTestDto> getIssuableRapidTests() {
        log.info("Loading issuable rapid tests");
        List<RapidTest> rapidTests = this.rapidTestRepository.findAllActiveAndChIssuable();
        return IssuableRapidTestMapper.fromRapidTests(rapidTests);
    }

    @Cacheable(VACCINE_CACHE_NAME)
    public List<VaccineDto> getVaccines() {
        log.info("Loading vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllValid();
        return VaccineMapper.uniqueVaccines(vaccines);
    }

    @Cacheable(ISSUABLE_VACCINE_NAME)
    public List<IssuableVaccineDto> getIssuableVaccines() {
        log.info("Loading all issuable vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllValid();
        return IssuableVaccineMapper.fromVaccines(vaccines);
    }

    @Cacheable(API_GATEWAY_ISSUABLE_VACCINE_CACHE_NAME)
    public List<IssuableVaccineDto> getApiGatewayIssuableVaccines() {
        log.info("Loading api gateway issuable vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllGatewayApiActive();
        return IssuableVaccineMapper.fromVaccines(vaccines);
    }

    @Cacheable(WEB_UI_ISSUABLE_VACCINE_CACHE_NAME)
    public List<IssuableVaccineDto> getWebUiIssuableVaccines() {
        log.info("Loading web ui issuable vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllWebUiActive();
        return IssuableVaccineMapper.fromVaccines(vaccines);
    }

    @Cacheable(API_PLATFORM_ISSUABLE_VACCINE_CACHE_NAME)
    public List<IssuableVaccineDto> getApiPlatformIssuableVaccines() {
        log.info("Loading platform api issuable vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllPlatformApiActive();
        return IssuableVaccineMapper.fromVaccines(vaccines);
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = RAPID_TEST_CACHE_NAME, allEntries = true)
    public void cleanRapidTestsCache() {
        log.info("Cleaning cache of rapid tests");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = ISSUABLE_TEST_CACHE_NAME, allEntries = true)
    public void cleanIssuableRapidTestsCache() {
        log.info("Cleaning cache of issuable rapid tests");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = VACCINE_CACHE_NAME, allEntries = true)
    public void cleanVaccinesCache() {
        log.info("Cleaning cache of vaccines");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = ISSUABLE_VACCINE_NAME, allEntries = true)
    public void cleanIssuableVaccinesCache() {
        log.info("Cleaning cache of issuable vaccines");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = API_GATEWAY_ISSUABLE_VACCINE_CACHE_NAME, allEntries = true)
    public void cleanApiIssuableVaccinesCache() {
        log.info("Cleaning cache of api issuable vaccines");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = WEB_UI_ISSUABLE_VACCINE_CACHE_NAME, allEntries = true)
    public void cleanWebIssuableVaccinesCache() {
        log.info("Cleaning cache of web issuable vaccines");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = VALUE_SETS_CACHE_NAME, allEntries = true)
    public void cleanValueSetsCache() {
        log.info("Cleaning cache of value sets");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = EXTENDED_VALUE_SETS_CACHE_NAME, allEntries = true)
    public void cleanExtendedValueSetsCache() {
        log.info("Cleaning cache of extended value sets");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = ISSUABLE_VACCINE_DTO_CACHE_NAME, allEntries = true)
    public void cleanIssuableVaccineDtoCache() {
        log.info("Cleaning cache of issuable vaccine dto");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = ISSUABLE_TEST_DTO_CACHE_NAME, allEntries = true)
    public void cleanIssuableTestDtoCache() {
        log.info("Cleaning cache of issuable test dto");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = COUNTRY_CODES_CACHE_NAME, allEntries = true)
    public void cleanCountryCodesCache() {
        log.info("Cleaning cache of country codes");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = COUNTRY_CODES_FOR_LANGUAGE_CACHE_NAME, allEntries = true)
    public void cleanCountryCodeByLanguageCache() {
        log.info("Cleaning cache of country code by language list");
    }

}
