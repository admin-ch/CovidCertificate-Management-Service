package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.IssuableRapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.IssuableVaccineMapper;
import ch.admin.bag.covidcertificate.api.mapper.RapidTestMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccineMapper;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TYP_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.NONE_PCR_TYPE_CODE;
import static ch.admin.bag.covidcertificate.api.Constants.PCR_TYPE_CODE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.DE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.EN;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.FR;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.IT;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.RM;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValueSetsService {

    private final CountryCodesLoader countryCodesLoader;
    private final VaccineRepository vaccineRepository;
    private final RapidTestRepository rapidTestRepository;

    public ValueSetsDto getValueSets() {
        var countryCodes = countryCodesLoader.getCountryCodes();
        return new ValueSetsDto(countryCodes, this.getIssuableVaccines(), this.getIssuableRapidTests());
    }

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

    public IssuableTestDto getIssuableTestDto(String testTypeCode, String testCode) {
        return getRapidTestDto(this.getIssuableRapidTests(), testTypeCode, testCode);
    }

    public IssuableTestDto getIssuableTestDto(TestCertificateDataDto testCertificateDataDto) {
        return getRapidTestDto(this.getIssuableRapidTests(), testCertificateDataDto.getTypeCode(), testCertificateDataDto.getManufacturerCode());
    }

    private IssuableTestDto getRapidTestDto(Collection<IssuableTestDto> testValueSets, String testTypeCode, String testCode) {
        if (!validPCRTest(testTypeCode, testCode) && !validNonPCRTest(testTypeCode, testCode)) {
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }

        var testValueSet = testValueSets
                .stream()
                .filter(issuableTestDto ->
                        (validPCRTest(testTypeCode, testCode) && issuableTestDto.getCode().equals(PCR_TYPE_CODE)) || (validNonPCRTest(testTypeCode, testCode) && issuableTestDto.getCode().equals(testCode)))
                .findFirst()
                .orElse(null);

        if (testValueSet == null) {
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }
        return testValueSet;
    }

    private boolean validPCRTest(String testTypeCode, String testCode) {
        return Objects.equals(testTypeCode, PCR_TYPE_CODE) && !StringUtils.hasText(testCode);
    }

    private boolean validNonPCRTest(String testTypeCode, String testCode) {
        return (Objects.equals(testTypeCode, NONE_PCR_TYPE_CODE)
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
        var countryCodes = getValueSets().getCountryCodes();
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

    @Transactional
    public List<TestDto> getRapidTests() {
        List<RapidTest> rapidTests = this.rapidTestRepository.findAllActive();
        return RapidTestMapper.fromRapidTests(rapidTests);
    }

    @Transactional
    public List<IssuableTestDto> getIssuableRapidTests() {
        List<RapidTest> rapidTests = this.rapidTestRepository.findAllActiveAndChIssuable();
        return IssuableRapidTestMapper.fromRapidTests(rapidTests);
    }

    @Transactional
    public List<VaccineDto> getVaccines() {
        List<Vaccine> vaccines = this.vaccineRepository.findAllActive();
        return VaccineMapper.fromVaccines(vaccines);
    }

    @Transactional
    public List<IssuableVaccineDto> getIssuableVaccines() {
        List<Vaccine> vaccines = this.vaccineRepository.findAllActiveAndChIssuable();
        return IssuableVaccineMapper.fromVaccines(vaccines);
    }
}
