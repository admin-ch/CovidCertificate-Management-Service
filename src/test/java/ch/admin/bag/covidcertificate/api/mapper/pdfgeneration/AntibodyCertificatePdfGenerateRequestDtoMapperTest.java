package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.pdf.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AntibodyCertificatePdfGenerateRequestDtoMapperTest {

    private final JFixture fixture = new JFixture();
    private final String countryOfTest = "Schweiz";
    private final String countryOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsLanguage() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getLanguage(), actual.getLanguage());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(),
                actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(
                CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getSystem(),
                actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsDateOfFirstPositiveTestResult() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(
                incoming.getDecodedCert().getAntibodyInfo().get(0).getSampleDateTime().toLocalDate(),
                actual.getSampleDate());
    }

    @Test
    public void mapsCountryOfTest() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTest, actual.getCountryOfTest());
    }

    @Test
    public void mapsCountryOfTestEn() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTestEn, actual.getCountryOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsUvci() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(
                incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getAntibodyInfo().get(0).getIdentifier(), actual.getIdentifier());
    }
}
