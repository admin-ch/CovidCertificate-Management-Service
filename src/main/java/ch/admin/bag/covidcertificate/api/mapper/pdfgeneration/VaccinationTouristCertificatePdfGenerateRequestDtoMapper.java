package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationTouristCertificatePdf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationTouristCertificatePdfGenerateRequestDtoMapper {

    public static VaccinationTouristCertificatePdf toVaccinationTouristCertificatePdf(
            VaccinationTouristCertificatePdfGenerateRequestDto vaccinationTouristCertificateCreateDto,
            IssuableVaccineDto vaccinationValueSet,
            String countryOfVaccinationDisplay,
            String countryOfVaccinationDisplayEn
    ) {
        var diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new VaccinationTouristCertificatePdf(
                vaccinationTouristCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                vaccinationTouristCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                vaccinationTouristCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                vaccinationTouristCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                vaccinationValueSet.getProphylaxisDisplay(),
                vaccinationValueSet.getProductDisplay(),
                vaccinationValueSet.getAuthHolderDisplay(),
                vaccinationTouristCertificateCreateDto.getDecodedCert().getVaccinationTouristInfo().get(0).getNumberOfDoses(),
                vaccinationTouristCertificateCreateDto.getDecodedCert().getVaccinationTouristInfo().get(0).getTotalNumberOfDoses(),
                vaccinationTouristCertificateCreateDto.getDecodedCert().getVaccinationTouristInfo().get(0).getVaccinationDate(),
                countryOfVaccinationDisplay,
                countryOfVaccinationDisplayEn,
                ISSUER,
                vaccinationTouristCertificateCreateDto.getDecodedCert().getVaccinationTouristInfo().get(0).getIdentifier());
    }
}
