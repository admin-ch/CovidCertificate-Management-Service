package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.service.domain.TestCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCertificateQrCodeMapperTest {

    private final JFixture jFixture = new JFixture();
    private final TestCertificateCreateDto incoming = jFixture.create(TestCertificateCreateDto.class);
    private final TestValueSet testValueSet = jFixture.create(TestValueSet.class);

    @Test
    public void mapsFamilyName() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getPersonData().getName().getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getPersonData().getName().getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getPersonData().getDateOfBirth());
    }

    @Test
    public void mapsTypeOfTest() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(testValueSet.getTypeCode(), actual.getTestInfo().get(0).getTypeOfTest());
    }

    @Test
    public void mapsTestName() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(testValueSet.getName(), actual.getTestInfo().get(0).getTestName());
    }

    @Test
    public void mapsTestManufacturer() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(testValueSet.getManufacturerCodeEu(), actual.getTestInfo().get(0).getTestManufacturer());
    }

    @Test
    public void mapsSampleDateTime() {
        ZonedDateTime sampleDateTime = ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 4, 16, 25, 12), SWISS_TIMEZONE);
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(getTestCertificateCreateDto("", "", "de"), testValueSet);
        assertEquals(sampleDateTime, actual.getTestInfo().get(0).getSampleDateTime());
    }

    @Test
    public void mapsTestingCentreOrFacility() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getTestInfo().get(0).getTestingCentreOrFacility(), actual.getTestInfo().get(0).getTestingCentreOrFacility());
    }

    @Test
    public void mapsMemberStateOfTest() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getTestInfo().get(0).getMemberStateOfTest(), actual.getTestInfo().get(0).getMemberStateOfTest());
    }

    @Test
    public void mapsVersion() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(VERSION, actual.getVersion());
    }

    @Test
    public void mapsIssuer() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(ISSUER, actual.getTestInfo().get(0).getIssuer());
    }

    @Test
    public void mapsIdentifier() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertNotNull(actual.getTestInfo().get(0).getIdentifier());
    }
}
