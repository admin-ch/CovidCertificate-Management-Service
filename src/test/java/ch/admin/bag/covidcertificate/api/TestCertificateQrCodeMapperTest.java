package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.TestCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.service.domain.qrcode.TestCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestCertificateQrCodeMapperTest {

    private final JFixture fixture = new JFixture();
    private TestCertificateCreateDto incoming;
    private IssuableTestDto testValueSet;

    @BeforeEach
    void setUp() {
        customizeTestValueSet(fixture);
        incoming = fixture.create(TestCertificateCreateDto.class);
        testValueSet = fixture.create(IssuableTestDto.class);
    }

    @Test
    void mapsFamilyName() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getPersonData().getName().getFamilyName());
    }

    @Test
    void mapsGivenName() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getPersonData().getName().getGivenName());
    }

    @Test
    void mapsDateOfBirth() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getPersonData().getDateOfBirth());
    }

    @Test
    void mapsTypeOfTest() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(testValueSet.getTestType().typeCode, actual.getTestInfo().get(0).getTypeOfTest());
    }

    @Test
    void mapsSampleDateTime() {
        ZonedDateTime sampleDateTime = ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 4, 16, 25, 12), SWISS_TIMEZONE);
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(getTestCertificateCreateDto("", "", "de"), testValueSet);
        assertEquals(sampleDateTime, actual.getTestInfo().get(0).getSampleDateTime());
    }

    @Test
    void mapsTestingCentreOrFacility() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getTestInfo().get(0).getTestingCentreOrFacility(), actual.getTestInfo().get(0).getTestingCentreOrFacility());
    }

    @Test
    void mapsMemberStateOfTest() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(incoming.getTestInfo().get(0).getMemberStateOfTest(), actual.getTestInfo().get(0).getMemberStateOfTest());
    }

    @Test
    void mapsVersion() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(VERSION, actual.getVersion());
    }

    @Test
    void mapsIssuer() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertEquals(ISSUER, actual.getTestInfo().get(0).getIssuer());
    }

    @Test
    void mapsIdentifier() {
        TestCertificateQrCode actual = TestCertificateQrCodeMapper.toTestCertificateQrCode(incoming, testValueSet);
        assertNotNull(actual.getTestInfo().get(0).getIdentifier());
    }
}
