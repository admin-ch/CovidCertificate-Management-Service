package ch.admin.bag.covidcertificate.service.domain;

import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode
public class VaccinationCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final String vaccineProphylaxis;
    private final String medicinalProduct;
    private final String marketingAuthorizationHolder;
    private final Integer numberOfDoses;
    private final Integer totalNumberOfDoses;
    private final LocalDate vaccinationDate;
    private final String countryOfVaccination;
    private final String countryOfVaccinationEn;

    private final String issuer;

    public VaccinationCertificatePdf(
            String familyName,
            String givenName,
            LocalDate dateOfBirth,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            String vaccineProphylaxis,
            String medicinalProduct,
            String marketingAuthorizationHolder,
            Integer numberOfDoses,
            Integer totalNumberOfDoses,
            LocalDate vaccinationDate,
            String countryOfVaccination,
            String countryOfVaccinationEn,
            String issuer,
            String identifier
    ) {
        super(familyName, givenName, dateOfBirth, identifier, language);
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.vaccineProphylaxis = vaccineProphylaxis;
        this.medicinalProduct = medicinalProduct;
        this.marketingAuthorizationHolder = marketingAuthorizationHolder;
        this.numberOfDoses = numberOfDoses;
        this.totalNumberOfDoses = totalNumberOfDoses;
        this.vaccinationDate = vaccinationDate;
        this.countryOfVaccination = countryOfVaccination;
        this.countryOfVaccinationEn = countryOfVaccinationEn;
        this.issuer = issuer;
    }

    public boolean isPartialVaccination() {
        return this.numberOfDoses < this.totalNumberOfDoses;
    }

}
