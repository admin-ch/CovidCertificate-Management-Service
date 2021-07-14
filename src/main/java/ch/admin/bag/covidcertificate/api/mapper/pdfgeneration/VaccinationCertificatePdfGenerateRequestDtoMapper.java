package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VaccinationCertificatePdfGenerateRequestDtoMapper {

    public static VaccinationCertificatePdf toVaccinationCertificatePdf(
            VaccinationCertificatePdfGenerateRequestDto vaccinationCertificateCreateDto,
            VaccinationValueSet vaccinationValueSet,
            String countryOfVaccinationDisplay,
            String countryOfVaccinationDisplayEn
    ) {
        CovidCertificateDiseaseOrAgentTargeted diseaseOrAgentTargeted = CovidCertificateDiseaseOrAgentTargeted.getStandardInstance();
        return new VaccinationCertificatePdf(
                vaccinationCertificateCreateDto.getDecodedCert().getPersonData().getName().getFamilyName(),
                vaccinationCertificateCreateDto.getDecodedCert().getPersonData().getName().getGivenName(),
                vaccinationCertificateCreateDto.getDecodedCert().getPersonData().getDateOfBirth(),
                vaccinationCertificateCreateDto.getLanguage(),
                diseaseOrAgentTargeted.getCode(),
                diseaseOrAgentTargeted.getSystem(),
                vaccinationValueSet.getProphylaxis(),
                vaccinationValueSet.getMedicinalProduct(),
                vaccinationValueSet.getAuthHolder(),
                vaccinationCertificateCreateDto.getDecodedCert().getVaccinationInfo().get(0).getNumberOfDoses(),
                vaccinationCertificateCreateDto.getDecodedCert().getVaccinationInfo().get(0).getTotalNumberOfDoses(),
                vaccinationCertificateCreateDto.getDecodedCert().getVaccinationInfo().get(0).getVaccinationDate(),
                countryOfVaccinationDisplay,
                countryOfVaccinationDisplayEn,
                ISSUER,
                vaccinationCertificateCreateDto.getDecodedCert().getVaccinationInfo().get(0).getIdentifier());
    }
}
