package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.RecoveryCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.TestCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.mapper.pdfgeneration.VaccinationCertificatePdfGenerateRequestDtoMapper;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CovidCertificatePdfGenerateRequestDtoMapperService {
    private final ValueSetsService valueSetsService;

    public VaccinationCertificatePdf toVaccinationCertificatePdf(VaccinationCertificatePdfGenerateRequestDto pdfGenerateRequestDto){
        var vaccinationValueSet = valueSetsService.getVaccinationValueSet(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getMedicinalProduct());
        var countryCode = valueSetsService.getCountryCode(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination(), pdfGenerateRequestDto.getLanguage());
        var countryCodeEn = valueSetsService.getCountryCodeEn(pdfGenerateRequestDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(pdfGenerateRequestDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificatePdfGenerateRequestDto pdfGenerateRequestDto) {
        var testValueSet = valueSetsService.getTestValueSet(pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTypeOfTest(), pdfGenerateRequestDto.getDecodedCert().getTestInfo().get(0).getTestManufacturer());
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
}
