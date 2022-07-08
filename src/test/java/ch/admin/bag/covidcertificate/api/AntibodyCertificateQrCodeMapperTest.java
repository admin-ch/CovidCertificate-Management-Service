package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.AntibodyCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AntibodyCertificateQrCodeMapperTest {

    private final JFixture fixture = new JFixture();
    private AntibodyCertificateCreateDto incoming;
    private IssuableTestDto testValueSet;

    @BeforeEach
    void setUp() {
        customizeTestValueSet(fixture);
        incoming = fixture.create(AntibodyCertificateCreateDto.class);
    }

    @Test
    void mapsFamilyName() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getPersonData().getName().getFamilyName());
    }

    @Test
    void mapsGivenName() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getPersonData().getName().getGivenName());
    }

    @Test
    void mapsDateOfBirth() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getPersonData().getDateOfBirth());
    }

    @Test
    void mapsSampleDateTime() {
        ZonedDateTime sampleDateTime = ZonedDateTime.of(LocalDateTime.of(2021, Month.NOVEMBER, 17, 0, 0, 0),
                                                        ZoneId.systemDefault());
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(
                getAntibodyCertificateCreateDto("de"));
        assertEquals(sampleDateTime, actual.getAntibodyInfo().get(0).getSampleDateTime());
    }

    @Test
    void mapsTestingCentreOrFacility() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(incoming.getAntibodyInfo().get(0).getTestingCenterOrFacility(), actual.getAntibodyInfo().get(0).getTestingCentreOrFacility());
    }

    @Test
    void mapsVersion() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(VERSION, actual.getVersion());
    }

    @Test
    void mapsIssuer() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertEquals(ISSUER, actual.getAntibodyInfo().get(0).getIssuer());
    }

    @Test
    void mapsIdentifier() {
        AntibodyCertificateQrCode actual = AntibodyCertificateQrCodeMapper.toAntibodyCertificateQrCode(incoming);
        assertNotNull(actual.getAntibodyInfo().get(0).getIdentifier());
    }
}
