package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import java.time.ZoneId;

import static ch.admin.bag.covidcertificate.api.Constants.ANTIBODY_CERTIFICATE_VALIDITY_IN_DAYS;
import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AntibodyCertificatePdfGenerateRequestDtoMapperTest {

    private final JFixture fixture = new JFixture();
    private final String countryOfTest = "Schweiz";
    private final String countryOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsLanguage() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getLanguage(), actual.getLanguage());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(), actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getSystem(), actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsDateOfFirstPositiveTestResult() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getAntibodyInfo().get(0).getSampleDateTime(), actual.getSampleDate().atStartOfDay(ZoneId.systemDefault()));
    }

    @Test
    public void mapsCountryOfTest() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTest, actual.getCountryOfTest());
    }

    @Test
    public void mapsCountryOfTestEn() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTestEn, actual.getCountryOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsValidFrom() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getAntibodyInfo().get(0).getSampleDateTime().plusDays(ANTIBODY_CERTIFICATE_VALIDITY_IN_DAYS), actual.getSampleDate().atStartOfDay(ZoneId.systemDefault()));
    }

    @Test
    public void mapsValidUntil() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getAntibodyInfo().get(0).getSampleDateTime().plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS), actual.getSampleDate().atStartOfDay(ZoneId.systemDefault()));
    }

    @Test
    public void mapsUvci() {
        AntibodyCertificatePdfGenerateRequestDto incoming = fixture.create(AntibodyCertificatePdfGenerateRequestDto.class);
        AntibodyCertificatePdf actual = AntibodyCertificatePdfGenerateRequestDtoMapper.toAntibodyCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getAntibodyInfo().get(0).getIdentifier(), actual.getIdentifier());
    }
}
