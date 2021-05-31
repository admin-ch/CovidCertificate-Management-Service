package ch.admin.bag.covidcertificate.service.domain;

import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
@EqualsAndHashCode
public class TestCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final String typeOfTest;
    private final String testName;
    private final String testManufacturer;
    private final ZonedDateTime sampleDateTime;
    private final String result;
    private final String testingCentreOrFacility;
    private final String memberStateOfTest;
    private final String memberStateOfTestEn;

    private final String issuer;

    public TestCertificatePdf(
            CovidCertificatePersonDto personDto,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            String typeOfTest,
            String testName,
            String testManufacturer,
            ZonedDateTime sampleDateTime,
            String result,
            String testingCentreOrFacility,
            String memberStateOfTest,
            String memberStateOfTestEn,
            String issuer,
            String identifier
    ) {
        super(personDto.getName().getFamilyName(), personDto.getName().getGivenName(), personDto.getDateOfBirth(), identifier, language);
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.typeOfTest = typeOfTest;
        this.testName = testName;
        this.testManufacturer = testManufacturer;
        this.sampleDateTime = sampleDateTime;
        this.result = result;
        this.testingCentreOrFacility = testingCentreOrFacility;
        this.memberStateOfTest = memberStateOfTest;
        this.memberStateOfTestEn = memberStateOfTestEn;
        this.issuer = issuer;
    }

}
