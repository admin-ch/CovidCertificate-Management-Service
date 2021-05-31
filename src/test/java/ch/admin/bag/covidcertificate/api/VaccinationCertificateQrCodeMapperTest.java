package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificateQrCodeMapper;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static ch.admin.bag.covidcertificate.api.Constants.VERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VaccinationCertificateQrCodeMapperTest {

    private final JFixture jFixture = new JFixture();
    private final VaccinationCertificateCreateDto incoming = jFixture.create(VaccinationCertificateCreateDto.class);
    private final VaccinationValueSet vaccinationValueSet = jFixture.create(VaccinationValueSet.class);

    @Test
    public void mapsFamilyName() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getPersonData().getName().getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getPersonData().getName().getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getPersonData().getDateOfBirth());
    }

    @Test
    public void mapsVaccineProphylaxis() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(vaccinationValueSet.getProphylaxis(), actual.getVaccinationInfo().get(0).getVaccineProphylaxis());
    }

    @Test
    public void mapsMedicinalProduct() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(vaccinationValueSet.getMedicinalProductCode(), actual.getVaccinationInfo().get(0).getMedicinalProduct());
    }

    @Test
    public void mapsMarketingAuthorizationHolder() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(vaccinationValueSet.getAuthHolderCode(), actual.getVaccinationInfo().get(0).getMarketingAuthorizationHolder());
    }

    @Test
    public void mapsNumberOfDoses() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getVaccinationInfo().get(0).getNumberOfDoses(), actual.getVaccinationInfo().get(0).getNumberOfDoses());
    }

    @Test
    public void mapsTotalNumberOfDoses() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getVaccinationInfo().get(0).getTotalNumberOfDoses(), actual.getVaccinationInfo().get(0).getTotalNumberOfDoses());
    }

    @Test
    public void mapsVaccinationDate() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getVaccinationInfo().get(0).getVaccinationDate(), actual.getVaccinationInfo().get(0).getVaccinationDate());
    }

    @Test
    public void mapsCountryOfVaccination() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(incoming.getVaccinationInfo().get(0).getCountryOfVaccination(), actual.getVaccinationInfo().get(0).getCountryOfVaccination());
    }

    @Test
    public void mapsVersion() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(VERSION, actual.getVersion());
    }

    @Test
    public void mapsIssuer() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertEquals(ISSUER, actual.getVaccinationInfo().get(0).getIssuer());
    }

    @Test
    public void mapsIdentifier() {
        VaccinationCertificateQrCode actual = VaccinationCertificateQrCodeMapper.toVaccinationCertificateQrCode(incoming, vaccinationValueSet);
        assertNotNull(actual.getVaccinationInfo().get(0).getIdentifier());
    }
}