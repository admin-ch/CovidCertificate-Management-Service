package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AntibodyCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AntibodyCertificatePdfMapperTest {

    private final JFixture jFixture = new JFixture();
    private final AntibodyCertificateCreateDto incoming = jFixture.create(AntibodyCertificateCreateDto.class);
    private final AntibodyCertificateQrCode qrCode = jFixture.create(AntibodyCertificateQrCode.class);
    private final String countryOfTest = "Schweiz";
    private final String countryOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals("2.16.840.1.113883.6.96", actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals("840539006", actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsSampleDate() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getAntibodyInfo().get(0).getSampleDate(), actual.getSampleDate());
    }

    @Test
    public void mapsTestingCentreOrFacility() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getAntibodyInfo().get(0).getTestingCenterOrFacility(), actual.getTestingCentreOrFacility());
    }

    @Test
    public void mapsCountryOfTest() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTest, actual.getCountryOfTest());
    }

    @Test
    public void mapsCountryOfTestEn() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTestEn, actual.getCountryOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        AntibodyCertificatePdf actual = AntibodyCertificatePdfMapper.toAntibodyCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }
}