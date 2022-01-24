package ch.admin.bag.covidcertificate.service.domain;

import ch.admin.bag.covidcertificate.api.request.CertificateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class AntibodyCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final LocalDate sampleDate;
    private final String testingCentreOrFacility;
    private final String countryOfTest;
    private final String countryOfTestEn;

    private final String issuer;

    public AntibodyCertificatePdf(
            String familyName,
            String givenName,
            String dateOfBirth,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            LocalDate sampleDate,
            String testingCentreOrFacility,
            String countryOfTest,
            String countryOfTestEn,
            String issuer,
            String identifier
    ) {
        super(familyName, givenName, dateOfBirth, identifier, language,  CertificateType.ANTIBODY);
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.sampleDate = sampleDate;
        this.testingCentreOrFacility = testingCentreOrFacility;
        this.countryOfTest = countryOfTest;
        this.countryOfTestEn = countryOfTestEn;
        this.issuer = issuer;
    }


    public boolean showValidOnlyInSwitzerland() { return true; }
}
