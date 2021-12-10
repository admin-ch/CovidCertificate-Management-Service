package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IssuableVaccineMapperTest {

    private final JFixture jFixture = new JFixture();

    @Test
    public void mapsProductCode() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getCode(), actual.getProductCode());
    }

    @Test
    public void mapsProductDisplay() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getDisplay(), actual.getProductDisplay());
    }

    @Test
    public void mapsProphylaxisCode() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getProphylaxis().getCode(), actual.getProphylaxisCode());
    }

    @Test
    public void mapsProphylaxisDisplay() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getProphylaxis().getDisplay(), actual.getProphylaxisDisplay());
    }

    @Test
    public void mapsAuthHolderCode() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getAuthHolder().getCode(), actual.getAuthHolderCode());
    }

    @Test
    public void mapsAuthHolderDisplay() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getAuthHolder().getDisplay(), actual.getAuthHolderDisplay());
    }

    @Test
    public void mapsIssuable() {
        Vaccine incoming = jFixture.create(Vaccine.class);
        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(incoming);
        assertEquals(incoming.getIssuable(), actual.getIssuable());
    }

    @Test
    public void checkIsTouristVaccine() {
        Vaccine mock = Mockito.mock(Vaccine.class);
        Mockito.when(mock.isTouristVaccine()).thenReturn(true);

        IssuableVaccineDto actual = IssuableVaccineMapper.fromVaccine(mock);
        assertTrue(actual.isTouristVaccine());

        Mockito.when(mock.isTouristVaccine()).thenReturn(false);
        actual = IssuableVaccineMapper.fromVaccine(mock);
        assertFalse(actual.isTouristVaccine());
    }
}
