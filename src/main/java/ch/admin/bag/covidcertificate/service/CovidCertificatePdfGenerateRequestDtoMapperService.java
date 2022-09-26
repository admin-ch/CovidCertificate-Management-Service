package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.AntibodyCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.RecoveryCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.TestCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.VaccinationCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.pdf.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationTouristCertificatePdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.VACCINATION_TOURIST_PRODUCT_CODE_SUFFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificatePdfGenerateRequestDtoMapperService {
    private final ValueSetsService valueSetsService;

    private static String removeSuffixIfExists(String key, String suffix) {
        return key.endsWith(suffix)
                ? key.substring(0, key.length() - suffix.length())
                : key;
    }

    public VaccinationCertificatePdf toVaccinationCertificatePdf(VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        VaccinationCertificateHcertDecodedDataDto hcertDecodedDataDto = pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0);
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(hcertDecodedDataDto.getMedicinalProduct());
        var countryCode = valueSetsService.getCountryCode(hcertDecodedDataDto.getCountryOfVaccination(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(hcertDecodedDataDto.getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(pdfGenerateRequestDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(VaccinationTouristCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        VaccinationTouristCertificateHcertDecodedDataDto hcertDecodedDataDto = pdfGenerateRequestDto.getDecodedCert().getVaccinationTouristInfo().get(0);
        var medicinalProductWithoutSuffix = removeSuffixIfExists(hcertDecodedDataDto.getMedicinalProduct(), VACCINATION_TOURIST_PRODUCT_CODE_SUFFIX);
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(medicinalProductWithoutSuffix);
        var countryCode = valueSetsService.getCountryCode(hcertDecodedDataDto.getCountryOfVaccination(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(hcertDecodedDataDto.getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationTouristCertificatePdfGenerateRequestDtoMapper.toVaccinationTouristCertificatePdf(pdfGenerateRequestDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        TestCertificateHcertDecodedDataDto hcertDecodedDataDto = pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0);
        var testValueSet = valueSetsService.validateAndGetIssuableTestDto(hcertDecodedDataDto.getTypeOfTest(), hcertDecodedDataDto.getTestManufacturer());
        var countryCode = valueSetsService.getCountryCode(hcertDecodedDataDto.getMemberStateOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(hcertDecodedDataDto.getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(pdfGenerateRequestDto, testValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificatePdf toRecoveryCertificatePdf(RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        RecoveryCertificateHcertDecodedDataDto hcertDecodedDataDto = pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0);
        var countryCode = valueSetsService.getCountryCode(hcertDecodedDataDto.getCountryOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(hcertDecodedDataDto.getCountryOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(pdfGenerateRequestDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificatePdf toRecoveryRatCertificatePdf(RecoveryRatCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        RecoveryRatCertificateHcertDecodedDataDto hcertDecodedDataDto = pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0);
        var countryCode = valueSetsService.getCountryCode(hcertDecodedDataDto.getMemberStateOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(hcertDecodedDataDto.getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }
        return RecoveryRatCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(pdfGenerateRequestDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public AntibodyCertificatePdf toAntibodyCertificatePdf(AntibodyCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var countryCode = valueSetsService.getCountryCode(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(pdfGenerateRequestDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public ExceptionalCertificatePdf toExceptionalCertificatePdf(ExceptionalCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var countryCode = valueSetsService.getCountryCode(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND, pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(ISO_3166_1_ALPHA_2_CODE_SWITZERLAND);
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return ExceptionalCertificatePdfGenerateRequestDtoMapper.toExceptionalCertificatePdf(pdfGenerateRequestDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }
}
