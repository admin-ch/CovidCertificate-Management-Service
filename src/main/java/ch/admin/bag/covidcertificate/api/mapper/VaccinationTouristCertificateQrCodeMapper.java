package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateData;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationTouristCertificateQrCodeMapper {

    public static VaccinationTouristCertificateQrCode toVaccinationTouristCertificateQrCode(
            VaccinationTouristCertificateCreateDto vaccinationTouristCertificateCreateDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationTouristCertificateQrCode(
                VERSION,
                PersonMapper.toCovidCertificatePerson(vaccinationTouristCertificateCreateDto.getPersonData()),
                toVaccinationTouristCertificateDataList(
                        vaccinationTouristCertificateCreateDto.getVaccinationTouristInfo(),
                        issuableVaccineDto
                )
        );
    }

    private static List<VaccinationTouristCertificateData> toVaccinationTouristCertificateDataList(
            List<VaccinationTouristCertificateDataDto> vaccinationTouristCertificateDataDtoList,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return vaccinationTouristCertificateDataDtoList.stream().map(vaccinationTouristCertificateDataDto ->
                toVaccinationTouristCertificateData(
                        vaccinationTouristCertificateDataDto,
                        issuableVaccineDto
                )
        ).collect(Collectors.toList());
    }

    private static VaccinationTouristCertificateData toVaccinationTouristCertificateData(
            VaccinationTouristCertificateDataDto vaccinationTouristCertificateDataDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationTouristCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                issuableVaccineDto.getProphylaxisCode(),
                issuableVaccineDto.getProductCode().concat(Constants.VACCINATION_TOURIST_PRODUCT_CODE_SUFFIX),
                issuableVaccineDto.getAuthHolderCode(),
                vaccinationTouristCertificateDataDto.getNumberOfDoses(),
                vaccinationTouristCertificateDataDto.getTotalNumberOfDoses(),
                vaccinationTouristCertificateDataDto.getVaccinationDate(),
                vaccinationTouristCertificateDataDto.getCountryOfVaccination(),
                ISSUER,
                UVCI.generateUVCI(vaccinationTouristCertificateDataDto.toString())
        );
    }
}
