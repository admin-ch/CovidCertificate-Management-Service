package ch.admin.bag.covidcertificate.service.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class TestCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final String typeOfTest;
    private final String testNameAndManufacturer;
    private final ZonedDateTime sampleDateTime;
    private final String result;
    private final String testingCentreOrFacility;
    private final String memberStateOfTest;
    private final String memberStateOfTestEn;

    private final String issuer;

    public TestCertificatePdf(
            String familyName,
            String givenName,
            String dateOfBirth,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            String typeOfTest,
            String testNameAndManufacturer,
            ZonedDateTime sampleDateTime,
            String result,
            String testingCentreOrFacility,
            String memberStateOfTest,
            String memberStateOfTestEn,
            String issuer,
            String identifier
    ) {
        super(familyName, givenName, dateOfBirth, identifier, language, "test");
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.typeOfTest = typeOfTest;
        this.testNameAndManufacturer = testNameAndManufacturer;
        this.sampleDateTime = sampleDateTime;
        this.result = result;
        this.testingCentreOrFacility = testingCentreOrFacility;
        this.memberStateOfTest = memberStateOfTest;
        this.memberStateOfTestEn = memberStateOfTestEn;
        this.issuer = issuer;
    }

}
