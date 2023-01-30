package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.RecoveryCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.qrcode.RecoveryCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.DAYS_UNTIL_RECOVERY_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecoveryCertificatePdfMapperTest {

    private final JFixture jFixture = new JFixture();
    private final RecoveryCertificateQrCode qrCode = jFixture.create(RecoveryCertificateQrCode.class);
    private final String countryOfTest = "Schweiz";
    private final String countryOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsDateOfFirstPositiveTestResult() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult(), actual.getDateOfFirstPositiveTestResult());
    }

    @Test
    public void mapsCountryOfTest() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTest, actual.getCountryOfTest());
    }

    @Test
    public void mapsCountryOfTestEn() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTestEn, actual.getCountryOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsValidFrom() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult().plusDays(DAYS_UNTIL_RECOVERY_VALID), actual.getValidFrom());
    }

    @Test
    public void mapsValidUntil() {
        RecoveryCertificateCreateDto incoming = jFixture.create(RecoveryCertificateCreateDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfMapper.toRecoveryCertificatePdf(incoming, qrCode, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult().plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS), actual.getValidUntil());
    }
}