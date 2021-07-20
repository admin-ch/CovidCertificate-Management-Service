package ch.admin.bag.covidcertificate.api.mapper.pdfgeneration;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.valueset.VaccinationValueSet;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificateDiseaseOrAgentTargeted;
import ch.admin.bag.covidcertificate.service.domain.VaccinationCertificatePdf;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static ch.admin.bag.covidcertificate.api.Constants.ISSUER;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class VaccinationCertificatePdfGenerateRequestDtoMapperTest {

    private final JFixture jFixture = new JFixture();
    private final VaccinationCertificatePdfGenerateRequestDto incoming = jFixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
    private final VaccinationValueSet vaccinationValueSet = jFixture.create(VaccinationValueSet.class);
    private final String countryOfVaccination = "Schweiz";
    private final String countryOfVaccinationEn = "Switzerland";

    @Test
    public void mapsFamilyName() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getFamilyName(), actual.getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getName().getGivenName(), actual.getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getPersonData().getDateOfBirth(), actual.getDateOfBirth());
    }


    @Test
    public void mapsLanguage() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getLanguage(), actual.getLanguage());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedCode() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getCode(), actual.getDiseaseOrAgentTargetedCode());
    }

    @Test
    public void mapsDiseaseOrAgentTargetedSystem() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(CovidCertificateDiseaseOrAgentTargeted.getStandardInstance().getSystem(), actual.getDiseaseOrAgentTargetedSystem());
    }

    @Test
    public void mapsVaccineProphylaxis() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getProphylaxis(), actual.getVaccineProphylaxis());
    }

    @Test
    public void mapsMedicinalProduct() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getMedicinalProduct(), actual.getMedicinalProduct());
    }

    @Test
    public void mapsMarketingAuthorizationHolder() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(vaccinationValueSet.getAuthHolder(), actual.getMarketingAuthorizationHolder());
    }

    @Test
    public void mapsNumberOfDoses() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getVaccinationInfo().get(0).getNumberOfDoses(), actual.getNumberOfDoses());
    }

    @Test
    public void mapsTotalNumberOfDoses() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getVaccinationInfo().get(0).getTotalNumberOfDoses(), actual.getTotalNumberOfDoses());
    }

    @Test
    public void mapsVaccinationDate() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getVaccinationInfo().get(0).getVaccinationDate(), actual.getVaccinationDate());
    }

    @Test
    public void mapsCountryOfVaccination() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(countryOfVaccination, actual.getCountryOfVaccination());
    }

    @Test
    public void mapsCountryOfVaccinationEn() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(countryOfVaccinationEn, actual.getCountryOfVaccinationEn());
    }

    @Test
    public void mapsIssuer() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(ISSUER, actual.getIssuer());
    }

    @Test
    public void mapsUvci() {
        VaccinationCertificatePdf actual = VaccinationCertificatePdfGenerateRequestDtoMapper.toVaccinationCertificatePdf(incoming, vaccinationValueSet, countryOfVaccination, countryOfVaccinationEn);
        assertEquals(incoming.getDecodedCert().getVaccinationInfo().get(0).getIdentifier(), actual.getIdentifier());
    }
}