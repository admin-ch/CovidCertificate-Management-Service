package ch.admin.bag.covidcertificate.service.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RecoveryCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final LocalDate dateOfFirstPositiveTestResult;
    private final String countryOfTest;
    private final String countryOfTestEn;

    private final String issuer;
    private final LocalDate validFrom;
    private final LocalDate validUntil;

    public RecoveryCertificatePdf(
            String familyName,
            String givenName,
            String dateOfBirth,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            LocalDate dateOfFirstPositiveTestResult,
            String countryOfTest,
            String countryOfTestEn,
            String issuer,
            LocalDate validFrom,
            LocalDate validUntil,
            String identifier
    ) {
        super(familyName, givenName, dateOfBirth, identifier, language);
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.dateOfFirstPositiveTestResult = dateOfFirstPositiveTestResult;
        this.countryOfTest = countryOfTest;
        this.countryOfTestEn = countryOfTestEn;
        this.issuer = issuer;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }


}
