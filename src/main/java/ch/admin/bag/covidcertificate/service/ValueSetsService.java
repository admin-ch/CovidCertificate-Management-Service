package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        VaccinationValueSet vaccinationValueSet = getValueSets()
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

    public TestValueSet getTestValueSet(TestCertificateDataDto testCertificateDataDto) {
        if(!validPCRTest(testCertificateDataDto) && !validNonPCRTest(testCertificateDataDto)){
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }

        TestValueSet testValueSet = getValueSets()
                    .getTestSets()
                    .stream()
                    .filter(valueSet ->
                            (validPCRTest(testCertificateDataDto) && valueSet.getTypeCode().equals(PCR_TYPE_CODE)) ||
                            (validNonPCRTest(testCertificateDataDto) && valueSet.getManufacturerCodeEu().equals(testCertificateDataDto.getManufacturerCode())))
                    .findFirst()
                    .orElse(null);

        if (testValueSet == null) {
            throw new CreateCertificateException(INVALID_TYP_OF_TEST);
        }
        return testValueSet;
    }

    private boolean validPCRTest(TestCertificateDataDto testCertificateDataDto){
        return Objects.equals(testCertificateDataDto.getTypeCode(), PCR_TYPE_CODE) && !StringUtils.hasText(testCertificateDataDto.getManufacturerCode());
    }

    private boolean validNonPCRTest(TestCertificateDataDto testCertificateDataDto){
        return (Objects.equals(testCertificateDataDto.getTypeCode(), NONE_PCR_TYPE_CODE)
                || !StringUtils.hasText(testCertificateDataDto.getTypeCode()))
                && StringUtils.hasText(testCertificateDataDto.getManufacturerCode());
    }

    public CountryCode getCountryCodeEn(String countryShort) {
        return getCountryCode(countryShort, RM);
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
                result = countryCodes.getEn();
                break;
            default:
                break;
        }
        return result;
    }
}
