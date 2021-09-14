package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.IssuableRapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.IssuableVaccineMapper;
import ch.admin.bag.covidcertificate.api.mapper.RapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccineMapper;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TYP_OF_TEST;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueSetsService {

    private static final String VALUE_SETS_CACHE_NAME = "valueSets";
    private static final String ISSUABLE_VACCINE_DTO_CACHE_NAME = "issuableVaccineDto";
    private static final String ISSUABLE_TEST_DTO_CACHE_NAME = "issuableTestDto";
    private static final String VACCINE_CACHE_NAME = "vaccines";
    private static final String ISSUABLE_VACCINE_CACHE_NAME = "issuableVaccines";
    private static final String RAPID_TEST_CACHE_NAME = "rapidTests";
    private static final String ISSUABLE_TEST_CACHE_NAME = "issuableTests";

    private final CountryCodesLoader countryCodesLoader;
    private final VaccineRepository vaccineRepository;
    private final RapidTestRepository rapidTestRepository;

    @Cacheable(VALUE_SETS_CACHE_NAME)
    public ValueSetsDto getValueSets() {
        var countryCodes = countryCodesLoader.getCountryCodes();
        return new ValueSetsDto(countryCodes, this.getIssuableVaccines(), this.getIssuableRapidTests());
    }

    @Cacheable(ISSUABLE_VACCINE_DTO_CACHE_NAME)
    public IssuableVaccineDto getVaccinationValueSet(String productCode) {
        var vaccinationValueSet = this.getValueSets()
                .getVaccinationSets()
                .stream()
                .filter(valueSet -> valueSet.getProductCode().equals(productCode))
                .findFirst()
                .orElse(null);
        if (vaccinationValueSet == null) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return vaccinationValueSet;
    }

    @Cacheable(ISSUABLE_TEST_DTO_CACHE_NAME)
    public IssuableTestDto getIssuableTestDto(String testTypeCode, String testCode) {
        return getRapidTestDto(this.getIssuableRapidTests(), testTypeCode, testCode);
    }

    @Cacheable(ISSUABLE_TEST_DTO_CACHE_NAME)
    public IssuableTestDto getIssuableTestDto(TestCertificateDataDto testCertificateDataDto) {
        return getRapidTestDto(this.getIssuableRapidTests(), testCertificateDataDto.getTypeCode(), testCertificateDataDto.getManufacturerCode());
    }

    private IssuableTestDto getRapidTestDto(Collection<IssuableTestDto> testValueSets, String testTypeCode, String testCode) {
        if (validPCRTest(testTypeCode, testCode)) {
            return new IssuableTestDto("", "PCR", TestType.PCR);
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

    private List<CountryCode> getCountryCodesForLanguage(String language) {
        var countryCodes = countryCodesLoader.getCountryCodes();
        List<CountryCode> result = Collections.emptyList();
        switch (language) {
            case DE:
                result = countryCodes.getDe();
                break;
            case IT:
                result = countryCodes.getIt();
                break;
            case FR:
                result = countryCodes.getFr();
                break;
            case RM:
                result = countryCodes.getRm();
                break;
            case EN:
                result = countryCodes.getEn();
                break;
            default:
                break;
        }
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
        List<Vaccine> vaccines = this.vaccineRepository.findAll();
        return VaccineMapper.fromVaccines(vaccines);
    }

    @Cacheable(ISSUABLE_VACCINE_CACHE_NAME)
    public List<IssuableVaccineDto> getIssuableVaccines() {
        log.info("Loading issuable vaccines");
        List<Vaccine> vaccines = this.vaccineRepository.findAllActiveAndChIssuable();
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
    @CacheEvict(value = ISSUABLE_VACCINE_CACHE_NAME, allEntries = true)
    public void cleanIssuableVaccinesCache() {
        log.info("Cleaning cache of issuable vaccines");
    }

    @Scheduled(fixedRateString = "${cc-management-service.cache-duration}")
    @CacheEvict(value = VALUE_SETS_CACHE_NAME, allEntries = true)
    public void cleanValueSetsCache() {
        log.info("Cleaning cache of value sets");
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

}
