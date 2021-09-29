package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateDtoMapperService {
    private final String SWITZERLAND = "CH";
    private final ValueSetsService valueSetsService;

    public void validate(VaccinationCertificateCreateDto createDto) {
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getVaccinationInfo().get(0).getCountryOfVaccination());
        final boolean isCountryCH = SWITZERLAND.equalsIgnoreCase(countryCodeEn.getShortName());
        final String productCode = createDto.getVaccinationInfo().get(0).getMedicinalProductCode();
        switch (createDto.getSystemSource()) {
            case WebUI:{
                var issuableVaccinesOpt = valueSetsService.getWebUiIssuableVaccines()
                                                          .stream()
                                                          .filter(issuableVaccine -> issuableVaccine.getProductCode()
                                                                                                    .equals(productCode))
                                                          .findFirst();
                boolean issuable = (issuableVaccinesOpt.isPresent()
                        ? false
                        : true /* issuableVaccinesOpt.get().isCHIssuable */);
                // a product not issueable in switzerland has been used
                if (/* !issuable && */ SWITZERLAND.equalsIgnoreCase(countryCodeEn.getShortName())) {
                    throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
                }
                break;
            }
            case CsvUpload:
            case ApiGateway:{
                // var issuableVaccinesOpt = valueSetsService.getApiGatewayIssuableVaccines()
                //     .stream().filter(issuableVaccine -> issuableVaccine.getProductCode().equals(productCode)).findFirst();
                // the product is issueable in switzerland only
                if (/* issuableVaccinesOpt.isPresent() && */ isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            case ApiPlatform:{
                boolean deleteMePlease = false;
                // var issuableVaccinesOpt = valueSetsService.getApiPlatformIssuableVaccines()
                //     .stream().filter(issuableVaccine -> issuableVaccine.getProductCode().equals(productCode)).findFirst();
                if (/* issuableVaccinesOpt.isPresent() && */ isCountryCH) {
                    throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
                }
                break;
            }
            default:
                throw new IllegalStateException("Attribute systemSource value is invalid. Check Request implementation and/or Dto Validation. ");
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
