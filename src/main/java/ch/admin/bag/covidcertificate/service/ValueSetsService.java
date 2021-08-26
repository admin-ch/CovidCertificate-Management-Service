package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.response.IssuableRapidTestDto;
import ch.admin.bag.covidcertificate.api.response.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.response.RapidTestDto;
import ch.admin.bag.covidcertificate.api.response.VaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.*;

@Service
@RequiredArgsConstructor
public class ValueSetsService {
    private final ValueSetsLoader valueSetsLoader;

    public ValueSetsDto getValueSets() {
        return valueSetsLoader.getValueSets();
    }

    public VaccinationValueSet getVaccinationValueSet(String medicinalProductCode) {
        var vaccinationValueSet = getValueSets()
                .getVaccinationSets()
                .stream()
                .filter(valueSet -> valueSet.getMedicinalProductCode().equals(medicinalProductCode))
                .findFirst()
                .orElse(null);
        if (vaccinationValueSet == null) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return vaccinationValueSet;
    }

    public TestValueSet getAllTestValueSet(String testTypeCode, String manufacturerCode) {
        return getTestValueSet(getValueSets().getAllTestValueSets(), testTypeCode, manufacturerCode);
    }

    public TestValueSet getChAcceptedTestValueSet(TestCertificateDataDto testCertificateDataDto) {
        return getTestValueSet(getValueSets().getChAcceptedTestValueSets(), testCertificateDataDto.getTypeCode(), testCertificateDataDto.getManufacturerCode());
    }

    private TestValueSet getTestValueSet(Collection<TestValueSet> testValueSets, String testTypeCode, String manufacturerCode) {
        if(!validPCRTest(testTypeCode, manufacturerCode) && !validNonPCRTest(testTypeCode, manufacturerCode)){
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }

        var testValueSet = testValueSets
                .stream()
                .filter(valueSet ->
                        (validPCRTest(testTypeCode, manufacturerCode) && valueSet.getTypeCode().equals(PCR_TYPE_CODE)) ||
                                (validNonPCRTest(testTypeCode, manufacturerCode) && valueSet.getManufacturerCodeEu().equals(manufacturerCode)))
                .findFirst()
                .orElse(null);

        if (testValueSet == null) {
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }
        return testValueSet;
    }

    private boolean validPCRTest(String testTypeCode, String manufacturerCode){
        return Objects.equals(testTypeCode, PCR_TYPE_CODE) && !StringUtils.hasText(manufacturerCode);
    }

    private boolean validNonPCRTest(String testTypeCode, String manufacturerCode){
        return (Objects.equals(testTypeCode, NONE_PCR_TYPE_CODE)
                || !StringUtils.hasText(testTypeCode))
                && StringUtils.hasText(manufacturerCode);
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

    public List<RapidTestDto> getRapidTests() {
        var rapidTests = new ArrayList<RapidTestDto>();
        rapidTests.add(new RapidTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)", true));
        rapidTests.add(new RapidTestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2", true));
        return rapidTests;
    }

    public List<IssuableRapidTestDto> getIssuableRapidTests() {
        var issuableRapidTests = new ArrayList<IssuableRapidTestDto>();
        issuableRapidTests.add(new IssuableRapidTestDto("1341", "Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)"));
        issuableRapidTests.add(new IssuableRapidTestDto("1065", "Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2"));
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
