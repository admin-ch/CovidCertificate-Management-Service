package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.ExceptionalCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.ExceptionalCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryRatCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.RecoveryRatCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.mapper.VaccinationTouristCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.*;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.service.domain.pdf.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AntibodyCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.qrcode.ExceptionalCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.qrcode.TestCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationTouristCertificateQrCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;


@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificateDtoMapperService {

    private final ValueSetsService valueSetsService;

    public VaccinationCertificateQrCode toVaccinationCertificateQrCode(VaccinationCertificateCreateDto createDto) {
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(
                createDto.getCertificateData().get(0).getMedicinalProductCode());
        return VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(createDto, vaccinationValueSet);
    }

    public VaccinationCertificateQrCode toVaccinationCertificateQrCodeForConversion(
            VaccinationCertificateConversionRequestDto conversionDto) {
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(
                conversionDto.getDecodedCert().getVaccinationInfo().get(0).getMedicinalProduct());
        return VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCodeForConversion(conversionDto,
                                                                                              vaccinationValueSet);
    }

    public VaccinationCertificatePdf toVaccinationCertificatePdf(
            VaccinationCertificateCreateDto createDto, VaccinationCertificateQrCode qrCodeData) {
        VaccinationCertificateDataDto vaccinationCertificateDataDto = createDto.getCertificateData().get(0);
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(
                vaccinationCertificateDataDto.getMedicinalProductCode());
        var countryCode = valueSetsService.getCountryCode(vaccinationCertificateDataDto.getCountryOfVaccination(),
                                                          createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(vaccinationCertificateDataDto.getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(createDto, vaccinationValueSet, qrCodeData,
                                                                           countryCode.getDisplay(),
                                                                           countryCodeEn.getDisplay());
    }

    public VaccinationTouristCertificateQrCode toVaccinationTouristCertificateQrCode(VaccinationTouristCertificateCreateDto createDto) {
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getVaccinationTouristInfo().get(0).getMedicinalProductCode());
        return VaccinationTouristCertificateQrCodeMapper.toVaccinationTouristCertificateQrCode(createDto, vaccinationValueSet);
    }

    public VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(VaccinationTouristCertificateCreateDto createDto, VaccinationTouristCertificateQrCode qrCodeData) {
        VaccinationCertificateDataDto vaccinationTouristCertificateDataDto = createDto.getVaccinationTouristInfo().get(0);
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(vaccinationTouristCertificateDataDto.getMedicinalProductCode());
        var countryCode = valueSetsService.getCountryCode(vaccinationTouristCertificateDataDto.getCountryOfVaccination(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(vaccinationTouristCertificateDataDto.getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationTouristCertificatePdfMapper.toVaccinationTouristCertificatePdf(createDto, vaccinationValueSet, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificateQrCode toTestCertificateQrCode(TestCertificateCreateDto createDto) {
        TestCertificateDataDto testCertificateDataDto = createDto.getTestInfo().get(0);
        var testValueSet = valueSetsService.validateAndGetIssuableTestDto(testCertificateDataDto.getTypeCode(), testCertificateDataDto.getManufacturerCode());
        return TestCertificateQrCodeMapper.toTestCertificateQrCode(createDto, testValueSet);

    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificateCreateDto createDto, TestCertificateQrCode qrCodeData) {
        TestCertificateDataDto testCertificateDataDto = createDto.getTestInfo().get(0);
        var testValueSet = valueSetsService.validateAndGetIssuableTestDto(testCertificateDataDto.getTypeCode(), testCertificateDataDto.getManufacturerCode());
        var countryCode = valueSetsService.getCountryCode(testCertificateDataDto.getMemberStateOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(testCertificateDataDto.getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return TestCertificatePdfMapper.toTestCertificatePdf(createDto, testValueSet, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificateQrCode toRecoveryCertificateQrCode(RecoveryCertificateCreateDto createDto) {
        return RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
    }

    public RecoveryCertificatePdf toRecoveryCertificatePdf(RecoveryCertificateCreateDto createDto, RecoveryCertificateQrCode qrCodeData) {
        RecoveryCertificateDataDto recoveryCertificateDataDto = createDto.getRecoveryInfo().get(0);
        var countryCode = valueSetsService.getCountryCode(recoveryCertificateDataDto.getCountryOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(recoveryCertificateDataDto.getCountryOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificateQrCode toRecoveryRatCertificateQrCode(RecoveryRatCertificateCreateDto createDto) {
        return RecoveryRatCertificateQrCodeMapper.toRecoveryCertificateQrCode(createDto);
    }

    public RecoveryCertificatePdf toRecoveryRatCertificatePdf(RecoveryRatCertificateCreateDto createDto, RecoveryCertificateQrCode qrCodeData) {
        RecoveryRatCertificateDataDto recoveryRatCertificateDataDto = createDto.getTestInfo().get(0);
        var countryCode = valueSetsService.getCountryCode(recoveryRatCertificateDataDto.getMemberStateOfTest(), createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(recoveryRatCertificateDataDto.getMemberStateOfTest());
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

    public ExceptionalCertificatePdf toExceptionalCertificatePdf(ExceptionalCertificateCreateDto createDto, ExceptionalCertificateQrCode qrCodeData) {
        var countryCode = valueSetsService.getCountryCode(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, createDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return ExceptionalCertificatePdfMapper.toExceptionalCertificatePdf(createDto, qrCodeData, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }
}
