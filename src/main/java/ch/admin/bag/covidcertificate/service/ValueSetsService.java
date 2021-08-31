package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.*;

@Service
@RequiredArgsConstructor
public class ValueSetsService {
    private final CountryCodesLoader countryCodesLoader;

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
        if (validPCRTest(testTypeCode, testCode)) {
            return new IssuableTestDto("", TestType.PCR.typeDisplay, TestType.PCR);
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

    public List<TestDto> getRapidTests() {
        var rapidTests = new ArrayList<TestDto>();
        rapidTests.add(new TestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)", TestType.RAPID_TEST, true));
        rapidTests.add(new TestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2", TestType.RAPID_TEST, true));
        return rapidTests;
    }

    public List<IssuableTestDto> getIssuableRapidTests() {
        var issuableRapidTests = new ArrayList<IssuableTestDto>();
        issuableRapidTests.add(new IssuableTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)", TestType.RAPID_TEST));
        issuableRapidTests.add(new IssuableTestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2", TestType.RAPID_TEST));
        return issuableRapidTests;
    }

    public List<VaccineDto> getVaccines() {
        var vaccines = new ArrayList<VaccineDto>();
        vaccines.add(new VaccineDto("EU/1/20/1528", "Comirnaty", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100030215", "Biontech Manufacturing GmbH", true));
        vaccines.add(new VaccineDto("EU/1/20/1507", "COVID-19 Vaccine Moderna", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100031184", "Moderna Biotech Spain S.L.", true));
        return vaccines;
    }

    public List<IssuableVaccineDto> getIssuableVaccines() {
        var issuableVaccines = new ArrayList<IssuableVaccineDto>();
        issuableVaccines.add(new IssuableVaccineDto("EU/1/20/1528", "Comirnaty", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100030215", "Biontech Manufacturing GmbH"));
        issuableVaccines.add(new IssuableVaccineDto("EU/1/20/1507", "COVID-19 Vaccine Moderna", "1119349007", "SARS-CoV-2 mRNA vaccine", "ORG-100031184", "Moderna Biotech Spain S.L."));
        return issuableVaccines;
    }
}
