package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.VaccinationCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationCertificatePdfMapper {

    public static VaccinationCertificatePdf toVaccinationCertificatePdf(
            VaccinationCertificateCreateDto vaccinationCertificateCreateDto,
            IssuableVaccineDto vaccinationValueSet,
            VaccinationCertificateQrCode qrCodeData,
            String countryOfVaccinationDisplay,
            String countryOfVaccinationDisplayEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new VaccinationCertificatePdf(
                vaccinationCertificateCreateDto.getPersonData().getName().getFamilyName(),
                vaccinationCertificateCreateDto.getPersonData().getName().getGivenName(),
                vaccinationCertificateCreateDto.getPersonData().getDateOfBirth(),
                vaccinationCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                vaccinationValueSet.getProphylaxisDisplay(),
                vaccinationValueSet.getProductDisplay(),
                vaccinationValueSet.getAuthHolderDisplay(),
                vaccinationCertificateCreateDto.getVaccinationInfo().get(0).getNumberOfDoses(),
                vaccinationCertificateCreateDto.getVaccinationInfo().get(0).getTotalNumberOfDoses(),
                vaccinationCertificateCreateDto.getVaccinationInfo().get(0).getVaccinationDate(),
                countryOfVaccinationDisplay,
                countryOfVaccinationDisplayEn,
                ISSUER,
                qrCodeData.getVaccinationInfo().get(0).getIdentifier());
    }
}
