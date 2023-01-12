package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.DAYS_UNTIL_RECOVERY_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecoveryCertificatePdfGenerateRequestDtoMapperTest {

    private final JFixture fixture = new JFixture();
    private final String countryOfTest = "Schweiz";
    private final String countryOfTestEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsLanguage() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getLanguage(), actual.getLanguage());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(), actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getSystem(), actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsDateOfFirstPositiveTestResult() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult(), actual.getDateOfFirstPositiveTestResult());
    }

    @Test
    public void mapsCountryOfTest() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTest, actual.getCountryOfTest());
    }

    @Test
    public void mapsCountryOfTestEn() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(countryOfTestEn, actual.getCountryOfTestEn());
    }

    @Test
    public void mapsIssuer() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsValidFrom() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult().plusDays(DAYS_UNTIL_RECOVERY_VALID), actual.getValidFrom());
    }

    @Test
    public void mapsValidUntil() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getRecoveryInfo().get(0).getDateOfFirstPositiveTestResult().plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS), actual.getValidUntil());
    }

    @Test
    public void mapsUvci() {
        RecoveryCertificatePdfGenerateRequestDto incoming = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
        RecoveryCertificatePdf actual = RecoveryCertificatePdfGenerateRequestDtoMapper.toRecoveryCertificatePdf(incoming, countryOfTest, countryOfTestEn);
        assertEquals(incoming.getDecodedCert().getRecoveryInfo().get(0).getIdentifier(), actual.getIdentifier());
    }
}