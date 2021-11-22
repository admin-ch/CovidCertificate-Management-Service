package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificateQrCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationTouristCertificatePdfMapper {

    public static VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(
            VaccinationTouristCertificateCreateDto vaccinationTouristCertificateCreateDto,
            IssuableVaccineDto vaccinationValueSet,
            VaccinationTouristCertificateQrCode qrCodeData,
            String countryOfVaccinationDisplay,
            String countryOfVaccinationDisplayEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new VaccinationTouristCertificatePdf(
                vaccinationTouristCertificateCreateDto.getPersonData().getName().getFamilyName(),
                vaccinationTouristCertificateCreateDto.getPersonData().getName().getGivenName(),
                vaccinationTouristCertificateCreateDto.getPersonData().getDateOfBirth(),
                vaccinationTouristCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                vaccinationValueSet.getProphylaxisDisplay(),
                vaccinationValueSet.getProductDisplay(),
                vaccinationValueSet.getAuthHolderDisplay(),
                vaccinationTouristCertificateCreateDto.getVaccinationTouristInfo().get(0).getNumberOfDoses(),
                vaccinationTouristCertificateCreateDto.getVaccinationTouristInfo().get(0).getTotalNumberOfDoses(),
                vaccinationTouristCertificateCreateDto.getVaccinationTouristInfo().get(0).getVaccinationDate(),
                countryOfVaccinationDisplay,
                countryOfVaccinationDisplayEn,
                ISSUER,
                qrCodeData.getVaccinationTouristInfo().get(0).getIdentifier());
    }
}
