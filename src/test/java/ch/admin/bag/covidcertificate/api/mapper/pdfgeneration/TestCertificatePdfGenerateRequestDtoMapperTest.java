package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestResult;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCertificatePdfGenerateRequestDtoMapperTest {

    private final JFixture fixture = new JFixture();
    private final TestCertificatePdfGenerateRequestDto incoming = fixture.create(TestCertificatePdfGenerateRequestDto.class);
    private final IssuableTestDto testValueSet = fixture.create(IssuableTestDto.class);
    private final String memberStateOfTest = "Schweiz";
    private final String memberStateOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsLanguage() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getLanguage(), actual.getLanguage());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(), actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getSystem(), actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsTypeOfTest() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(testValueSet.getTestType().typeDisplay, actual.getTypeOfTest());
    }

    @Test
    public void mapsTestName() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(testValueSet.getDisplay(), actual.getTestNameAndManufacturer());
    }

    @Test
    public void mapsSampleDateTime() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getTestInfo().get(0).getSampleDateTime(), actual.getSampleDateTime());
    }

    @Test
    public void mapsResult() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(TestResult.NEGATIVE.display, actual.getResult());
    }

    @Test
    public void mapsTestingCentreOrFacility() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getTestInfo().get(0).getTestingCentreOrFacility(), actual.getTestingCentreOrFacility());
    }

    @Test
    public void mapsMemberStateOfTest() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(memberStateOfTest, actual.getMemberStateOfTest());
    }

    @Test
    public void mapsMemberStateOfTestEn() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(memberStateOfTestEn, actual.getMemberStateOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsUvci() {
        TestCertificatePdf actual = TestCertificatePdfGenerateRequestDtoMapper.toTestCertificatePdf(incoming, testValueSet, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getDecodedCert().getTestInfo().get(0).getIdentifier(), actual.getIdentifier());
    }
}