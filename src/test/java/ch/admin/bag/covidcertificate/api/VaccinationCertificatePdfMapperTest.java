package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.VaccinationCertificatePdfMapper;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificateQrCode;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VaccinationCertificatePdfMapperTest {

    private final JFixture jFixture = new JFixture();
    private final VaccinationCertificateCreateDto incoming = jFixture.create(VaccinationCertificateCreateDto.class);
    private final VaccinationValueSet vaccinationValueSet = jFixture.create(VaccinationValueSet.class);
    private final VaccinationCertificateQrCode qrCode = jFixture.create(VaccinationCertificateQrCode.class);
    private final String countryOfVaccination = "Schweiz";
    private final String countryOfVaccinationEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    public void mapsVaccineProphylaxis() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getProphylaxis(), actual.getVaccineProphylaxis());
    }

    @Test
    public void mapsMedicinalProduct() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getMedicinalProduct(), actual.getMedicinalProduct());
    }

    @Test
    public void mapsMarketingAuthorizationHolder() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getAuthHolder(), actual.getMarketingAuthorizationHolder());
    }

    @Test
    public void mapsNumberOfDoses() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getVaccinationInfo().get(0).getNumberOfDoses(), actual.getNumberOfDoses());
    }

    @Test
    public void mapsTotalNumberOfDoses() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getVaccinationInfo().get(0).getTotalNumberOfDoses(), actual.getTotalNumberOfDoses());
    }

    @Test
    public void mapsVaccinationDate() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getVaccinationInfo().get(0).getVaccinationDate(), actual.getVaccinationDate());
    }

    @Test
    public void mapsCountryOfVaccination() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(countryOfVaccination, actual.getCountryOfVaccination());
    }

    @Test
    public void mapsCountryOfVaccinationEn() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(countryOfVaccinationEn, actual.getCountryOfVaccinationEn());
    }

    @Test
    public void mapsIssuer() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, qrCode, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(ISSUER, actual.getIssuer());
    }
}