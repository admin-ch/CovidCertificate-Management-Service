package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.mapper.PersonMapper;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersonMapperTest {

    private final JFixture jFixture = new JFixture();

    @Test
    public void mapsFamilyName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getFamilyName(), actual.getName().getFamilyName());
    }

    @Test
    public void mapsGivenName() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getName().getGivenName(), actual.getName().getGivenName());
    }

    @Test
    public void mapsStandardisedWith_Az09() {
        CovidCertificatePersonDto incoming = create("familyName", "givenName");
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals("FAMILYNAME", actual.getName().getFamilyNameStandardised());
        assertEquals("GIVENNAME", actual.getName().getGivenNameStandardised());
    }

    @Test
    public void mapsStandardisedWith_SpecialAz() {
        CovidCertificatePersonDto incoming = create("åÅäÄÆæöÖøØĲĳÜüßœŒÐ", "ÐŒœßüÜåÅĳĲØøÖöæÆÄä");
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals("AAAAAEAEAEAEOEOEOEOEIJIJUEUESSOEOED", actual.getName().getFamilyNameStandardised());
        assertEquals("DOEOESSUEUEAAAAIJIJOEOEOEOEAEAEAEAE", actual.getName().getGivenNameStandardised());
    }

    @Test
    public void mapsStandardisedWith_NonAz09() {
        CovidCertificatePersonDto incoming = create("f-a[m$i^l§y_N;a}m(e", "g/i|v#e&n%N~a!m\"e");
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals("F<A<M<I<LY<N<A<M<E", actual.getName().getFamilyNameStandardised());
        assertEquals("G<I<V<E<N<N<A<M<E", actual.getName().getGivenNameStandardised());
    }

    @Test
    public void mapsDateOfBirth() {
        CovidCertificatePersonDto incoming = jFixture.create(CovidCertificatePersonDto.class);
        CovidCertificatePerson actual = PersonMapper.toCovidCertificatePerson(incoming);
        assertEquals(incoming.getDateOfBirth(), actual.getDateOfBirth());
    }

    private CovidCertificatePersonDto create(String familyName, String givenName) {
        CovidCertificatePersonNameDto personName = new CovidCertificatePersonNameDto(familyName, givenName);
        CovidCertificatePersonDto person = new CovidCertificatePersonDto(personName, "01.01.1970");
        return person;
    }
}
