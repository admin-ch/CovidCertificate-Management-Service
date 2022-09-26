package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecoveryCertificateQrCodeMapperTest {

    private final JFixture jFixture = new JFixture();


    @Test
    public void mapsFamilyName() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(actual.getPersonData().getName().getFamilyName(), incoming.getPersonData().getName().getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getPersonData().getName().getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getPersonData().getDateOfBirth());
    }

    @Test
    public void mapsDateOfFirstPositiveTestResult() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(incoming.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult(), actual.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult());
    }

    @Test
    public void mapsCountryOfTest() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(incoming.getRecoveryInfo().get(0).getCountryOfTest(), actual.getRecoveryInfo().get(0).getCountryOfTest());
    }

    @Test
    public void mapsVersion() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificateQrCode actual = RecoveryCertificateQrCodeMapper.toRecoveryCertificateQrCode(incoming);
        assertEquals(VERSION, actual.getVersion());
    }
}