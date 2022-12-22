package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificateHcertDecodedDataDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationCertificateData;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.util.UVCI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

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
                PersonMapper.toCovidCertificatePerson(vaccinationCertificateCreateDto.getPersonData()),
                VaccinationCertificateQrCodeMapper
                        .toVaccinationCertificateDataList(
                                vaccinationCertificateCreateDto.getVaccinationInfo(),
                                issuableVaccineDto
                        )
        );
    }

    public static VaccinationCertificateQrCode toVaccinationCertificateQrCodeForConversion(
            VaccinationCertificateConversionRequestDto vaccinationCertificateCreateDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationCertificateQrCode(
                VERSION,
                PersonMapper.toCertificatePerson(
                        vaccinationCertificateCreateDto.getDecodedCert().getPersonData()),
                VaccinationCertificateQrCodeMapper.toVaccinationCertificateDataListForConversion(
                        vaccinationCertificateCreateDto.getDecodedCert().getVaccinationInfo(),
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
        ).toList();
    }

    private static List<VaccinationCertificateData> toVaccinationCertificateDataListForConversion(
            List<VaccinationCertificateHcertDecodedDataDto> vaccinationCertificateHcertDecodedDataDtoList
            ,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return vaccinationCertificateHcertDecodedDataDtoList
                .stream().map(vaccinationCertificateHcertDecodedDataDto -> toVaccinationCertificateData(
                        vaccinationCertificateHcertDecodedDataDto,
                        issuableVaccineDto))
                .toList();
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

    private static VaccinationCertificateData toVaccinationCertificateData(
            VaccinationCertificateHcertDecodedDataDto vaccinationCertificateHcertDecodedDataDto,
            IssuableVaccineDto issuableVaccineDto
    ) {
        return new VaccinationCertificateData(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                issuableVaccineDto.getProphylaxisCode(),
                issuableVaccineDto.getProductCode(),
                issuableVaccineDto.getAuthHolderCode(),
                vaccinationCertificateHcertDecodedDataDto.getNumberOfDoses(),
                vaccinationCertificateHcertDecodedDataDto.getTotalNumberOfDoses(),
                vaccinationCertificateHcertDecodedDataDto.getVaccinationDate(),
                vaccinationCertificateHcertDecodedDataDto.getCountryOfVaccination(),
                ISSUER,
                UVCI.generateUVCI(vaccinationCertificateHcertDecodedDataDto.toString())
        );
    }
}
