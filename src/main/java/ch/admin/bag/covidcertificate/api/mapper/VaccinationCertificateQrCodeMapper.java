package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateData;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationCertificateQrCodeMapper {

    public static VaccinationCertificateQrCode toVaccinationCertificateQrCode(
            VaccinationCertificateCreateDto vaccinationCertificateCreateDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationCertificateQrCode(
                VERSION,
                CovidCertificatePersonMapper.toCovidCertificatePerson(vaccinationCertificateCreateDto.getPersonData()),
                VaccinationCertificateQrCodeMapper
                        .toVaccinationCertificateDataList(
                                vaccinationCertificateCreateDto.getVaccinationInfo(),
                                issuableVaccineDto
                        )
        );
    }

    private static List<VaccinationCertificateData> toVaccinationCertificateDataList(
            List<VaccinationCertificateDataDto> vaccinationCertificateDataDtoList,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return vaccinationCertificateDataDtoList.stream().map(vaccinationCertificateDataDto ->
                toVaccinationCertificateData(
                        vaccinationCertificateDataDto,
                        issuableVaccineDto
                )
        ).collect(Collectors.toList());
    }

    private static VaccinationCertificateData toVaccinationCertificateData(
            VaccinationCertificateDataDto vaccinationCertificateDataDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                issuableVaccineDto.getProphylaxisCode(),
                issuableVaccineDto.getProductCode(),
                issuableVaccineDto.getAuthHolderCode(),
                vaccinationCertificateDataDto.getNumberOfDoses(),
                vaccinationCertificateDataDto.getTotalNumberOfDoses(),
                vaccinationCertificateDataDto.getVaccinationDate(),
                vaccinationCertificateDataDto.getCountryOfVaccination(),
                ISSUER,
                UVCI.generateUVCI(vaccinationCertificateDataDto.toString())
        );
    }
}
