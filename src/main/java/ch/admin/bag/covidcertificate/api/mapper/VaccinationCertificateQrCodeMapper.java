package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
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
            VaccinationValueSet vaccinationValueSet
    ) {
        return new VaccinationCertificateQrCode(
                VERSION,
                CovidCertificatePersonMapper.toCovidCertificatePerson(vaccinationCertificateCreateDto.getPersonData()),
                VaccinationCertificateQrCodeMapper
                        .toVaccinationCertificateDataList(
                                vaccinationCertificateCreateDto.getVaccinationInfo(),
                                vaccinationValueSet
                        )
        );
    }

    private static List<VaccinationCertificateData> toVaccinationCertificateDataList(
            List<VaccinationCertificateDataDto> vaccinationCertificateDataDtoList,
            VaccinationValueSet vaccinationValueSet
    ) {
        return vaccinationCertificateDataDtoList.stream().map(vaccinationCertificateDataDto ->
                toVaccinationCertificateData(
                        vaccinationCertificateDataDto,
                        vaccinationValueSet
                )
        ).collect(Collectors.toList());
    }

    private static VaccinationCertificateData toVaccinationCertificateData(
            VaccinationCertificateDataDto vaccinationCertificateDataDto,
            VaccinationValueSet vaccinationValueSet
    ) {
        return new VaccinationCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                vaccinationValueSet.getProphylaxisCode(),
                vaccinationValueSet.getMedicinalProductCode(),
                vaccinationValueSet.getAuthHolderCode(),
                vaccinationCertificateDataDto.getNumberOfDoses(),
                vaccinationCertificateDataDto.getTotalNumberOfDoses(),
                vaccinationCertificateDataDto.getVaccinationDate(),
                vaccinationCertificateDataDto.getCountryOfVaccination(),
                ISSUER,
                UVCI.generateUVCI(vaccinationCertificateDataDto.toString())
        );
    }
}
