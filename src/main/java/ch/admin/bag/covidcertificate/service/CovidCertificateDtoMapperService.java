package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.*;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;


@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateDtoMapperService {

    private final ValueSetsService valueSetsService;

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

    public VaccinationTouristCertificateQrCode toVaccinationTouristCertificateQrCode(VaccinationTouristCertificateCreateDto createDto) {
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode());
        return  VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(createDto, vaccinationValueSet);
    }

    public VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(VaccinationTouristCertificateCreateDto createDto, VaccinationTouristCertificateQrCode qrCodeData){
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode());
        var countryCode = valueSetsService.getCountryCode(createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getVaccinationTouristInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(createDto, vaccinationValueSet, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
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

    public RecoveryCertificateQrCode toRecoveryRatCertificateQrCode(RecoveryRatCertificateCreateDto createDto) {
//        RecoveryRatCertificateDataDto recoveryDataInfo = createDto.getTestInfo().get(0);
//        var testValueSet = valueSetsService.getIssuableTestDto(recoveryDataInfo.getTypeCode(), recoveryDataInfo.getManufacturerCode());
        return RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
    }

    public RecoveryCertificatePdf toRecoveryRatCertificatePdf(RecoveryRatCertificateCreateDto createDto, RecoveryCertificateQrCode qrCodeData) {
//        RecoveryRatCertificateDataDto recoveryDataInfo = createDto.getTestInfo().get(0);
//        var testValueSet = valueSetsService.getIssuableTestDto(recoveryDataInfo.getTypeCode(), recoveryDataInfo.getManufacturerCode());
        var countryCode = valueSetsService.getCountryCode(createDto.getTestInfo().get(0).getMemberStateOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getTestInfo().get(0).getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return RecoveryRatCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public AntibodyCertificateQrCode toAntibodyCertificateQrCode(AntibodyCertificateCreateDto createDto) {
        return AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(createDto);
    }

    public AntibodyCertificatePdf toAntibodyCertificatePdf(AntibodyCertificateCreateDto createDto, AntibodyCertificateQrCode qrCodeData) {
        var countryCode = valueSetsService.getCountryCode(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public ExceptionalCertificateQrCode toExceptionalCertificateQrCode(ExceptionalCertificateCreateDto createDto) {
        return ExceptionalCertificateQrCodeMapper.toExceptionalCertificateQrCode(createDto);
    }

    public ExceptionalCertificatePdf toExceptionalCertificatePdf(ExceptionalCertificateCreateDto createDto,ExceptionalCertificateQrCode qrCodeData) {
        var countryCode = valueSetsService.getCountryCode(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return ExceptionalCertificatePdfMapper.toExceptionalCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }
}
