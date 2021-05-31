package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.TestCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.service.domain.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCertificatePdfMapperTest {

    private final JFixture jFixture = new JFixture();
    private final TestCertificateCreateDto incoming = jFixture.create(TestCertificateCreateDto.class);
    private final TestValueSet testValueSet = jFixture.create(TestValueSet.class);
    private final TestCertificateQrCode qrCode = jFixture.create(TestCertificateQrCode.class);
    private final String memberStateOfTest = "Schweiz";
    private final String memberStateOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsTypeOfTest() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(testValueSet.getType(), actual.getTypeOfTest());
    }

    @Test
    public void mapsTestName() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(testValueSet.getName(), actual.getTestName());
    }

    @Test
    public void mapsTestManufacturer() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(testValueSet.getManufacturer(), actual.getTestManufacturer());
    }

    @Test
    public void mapsSampleDateTime() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getTestInfo().get(0).getSampleDateTime(), actual.getSampleDateTime());
    }

    @Test
    public void mapsTestingCentreOrFacility() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(incoming.getTestInfo().get(0).getTestingCentreOrFacility(), actual.getTestingCentreOrFacility());
    }

    @Test
    public void mapsMemberStateOfTest() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(memberStateOfTest, actual.getMemberStateOfTest());
    }

    @Test
    public void mapsMemberStateOfTestEn() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(memberStateOfTestEn, actual.getMemberStateOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        TestCertificatePdf actual = TestCertificatePdfMapper.toTestCertificatePdf(incoming, testValueSet, qrCode, memberStateOfTest, memberStateOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }
}