package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.Constants;
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
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.ExceptionalCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.RecoveryRatCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.*;

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
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getMedicinalProduct());
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(pdfGenerateRequestDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(VaccinationTouristCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var medicinalProductWithoutSuffix=removeSuffixIfExists(pdfGenerateRequestDto.getDecodedCert().getVaccinationTouristInfo().get(0).getMedicinalProduct(), VACCINATION_TOURIST_PRODUCT_CODE_SUFFIX);
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(medicinalProductWithoutSuffix);
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getVaccinationTouristInfo().get(0).getCountryOfVaccination(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getVaccinationTouristInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationTouristCertificatePdfGenerateRequestDtoMapper.toVaccinationTouristCertificatePdf(pdfGenerateRequestDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var testValueSet = valueSetsService.getIssuableTestDto(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTypeOfTest(), pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTestManufacturer());
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(pdfGenerateRequestDto, testValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificatePdf toRecoveryCertificatePdf(RecoveryCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0).getCountryOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getRecoveryInfo().get(0).getCountryOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(pdfGenerateRequestDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificatePdf toRecoveryRatCertificatePdf(RecoveryRatCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
//        var issuableTestDto = valueSetsService.getIssuableTestDto(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTypeOfTest(), pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTestManufacturer());
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest());
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
