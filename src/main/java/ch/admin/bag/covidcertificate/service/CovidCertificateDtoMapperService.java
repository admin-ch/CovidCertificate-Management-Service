package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.Issuable;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateDtoMapperService {

    private static final String SWITZERLAND = "CH";

    private final ValueSetsService valueSetsService;

    public void validate(VaccinationCertificateCreateDto createDto) {
        final boolean isCountryCH = SWITZERLAND.equalsIgnoreCase(createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
        final String productCode = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();

        switch (createDto.getSystemSource()) {
            case WebUI:{
                var issuableVaccine = retrieveProduct(productCode, valueSetsService.getWebUiIssuableVaccines());
                throwExceptionIfIssuableIsViolated(isCountryCH, issuableVaccine.getIssuable());
                break;
            }
            case CsvUpload:
            case ApiGateway:{
                // the source requires switzerland
                if (!isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case ApiPlatform: {
                // this source requires foreign countries
                if (isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
    }

    private IssuableVaccineDto retrieveProduct(String productCode, List<IssuableVaccineDto> issuableVaccineDtoList) {
        var issuableVaccinesOpt = issuableVaccineDtoList.stream()
                .filter(issuableVaccine -> issuableVaccine.getProductCode().equals(productCode)).findFirst();
        // the product is not available for this source
        if (issuableVaccinesOpt.isEmpty()) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
        return issuableVaccinesOpt.get();
    }

    private void throwExceptionIfIssuableIsViolated(boolean isCountryCH, Issuable issuable) {
        switch(issuable) {
            case CH_ONLY: {
                if (!isCountryCH) {
                    // a product issueable only in switzerland has been used
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case ABROAD_ONLY: {
                if (isCountryCH) {
                    // a product not issueable in switzerland has been used
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case CH_AND_ABROAD: {
                // a product is everywhere issuable
                break;
            }
            case UNDEFINED: {
                // issuable is not defined for this product
                throw new IllegalStateException("Issuable of Vaccines is undefined. ");
            }
        }
    }

    public VaccinationCertificateQrCode toVaccinationCertificateQrCode(VaccinationCertificateCreateDto createDto) {
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getVaccinationInfo().get(0).getMedicinalProductCode());
        return  VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(createDto, vaccinationValueSet);
    }

    public VaccinationCertificatePdf toVaccinationCertificatePdf(VaccinationCertificateCreateDto createDto, VaccinationCertificateQrCode qrCodeData){
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getVaccinationInfo().get(0).getMedicinalProductCode());
        var countryCode = valueSetsService.getCountryCode(createDto.getVaccinationInfo().get(0).getCountryOfVaccination(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto, vaccinationValueSet, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificateQrCode toTestCertificateQrCode(TestCertificateCreateDto createDto) {
        var testValueSet = valueSetsService.getIssuableTestDto(createDto.getTestInfo().get(0));
        return TestCertificateQrCodeMapper.toTestCertificateQrCode(createDto, testValueSet);

    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificateCreateDto createDto, TestCertificateQrCode qrCodeData) {
        var testValueSet = valueSetsService.getIssuableTestDto(createDto.getTestInfo().get(0));
        var countryCode = valueSetsService.getCountryCode(createDto.getTestInfo().get(0).getMemberStateOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getTestInfo().get(0).getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return TestCertificatePdfMapper.toTestCertificatePdf(createDto, testValueSet, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificateQrCode toRecoveryCertificateQrCode(RecoveryCertificateCreateDto createDto) {
        return RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
    }

    public RecoveryCertificatePdf toRecoveryCertificatePdf(RecoveryCertificateCreateDto createDto, RecoveryCertificateQrCode qrCodeData) {
        var countryCode = valueSetsService.getCountryCode(createDto.getRecoveryInfo().get(0).getCountryOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getRecoveryInfo().get(0).getCountryOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }
}
