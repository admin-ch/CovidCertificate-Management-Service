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

    public VaccinationCertificatePdf toVaccinationCertificatePdf(VaccinationCertificatePdfGenerateRequestDto createDto){
        VaccinationValueSet vaccinationValueSet = valueSetsService.getVaccinationValueSet(createDto.getDecodedCert().getVaccinationInfo().get(0).getMedicinalProduct());
        CountryCode countryCode = valueSetsService.getCountryCode(createDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination(), createDto.getLanguage());
        CountryCode countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getDecodedCert().getVaccinationInfo().get(0).getCountryOfVaccination());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
        return VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(createDto, vaccinationValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public TestCertificatePdf toTestCertificatePdf(TestCertificatePdfGenerateRequestDto createDto) {
        TestValueSet testValueSet = valueSetsService.getTestValueSet(createDto.getDecodedCert().getTestInfo().get(0).getTypeOfTest(), createDto.getDecodedCert().getTestInfo().get(0).getTestManufacturer());
        CountryCode countryCode = valueSetsService.getCountryCode(createDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest(), createDto.getLanguage());
        CountryCode countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getDecodedCert().getTestInfo().get(0).getMemberStateOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }

        return TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(createDto, testValueSet, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }

    public RecoveryCertificatePdf toRecoveryCertificatePdf(RecoveryCertificatePdfGenerateRequestDto createDto) {
        CountryCode countryCode = valueSetsService.getCountryCode(createDto.getDecodedCert().getRecoveryInfo().get(0).getCountryOfTest(), createDto.getLanguage());
        CountryCode countryCodeEn = valueSetsService.getCountryCodeEn(createDto.getDecodedCert().getRecoveryInfo().get(0).getCountryOfTest());
        if (countryCode == null || countryCodeEn == null) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_TEST);
        }

        return RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(createDto, countryCode.getDisplay(), countryCodeEn.getDisplay());
    }
}
