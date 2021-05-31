package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.CovidCertificatePersonMapper;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CovidCertificatePersonMapperTest {

    private final JFixture jFixture = new JFixture();


    @Test
    public void mapsFamilyName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getFamilyName(), actual.getName().getFamilyName());
    }


    @Test
    public void mapsGivenName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getGivenName(), actual.getName().getGivenName());
    }

    @Test
    public void mapsDateOfBirth() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = CovidCertificatePersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getDateOfBirth(), actual.getDateOfBirth());
    }
}