package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import com.flextrade.jfixture.JFixture;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DUPLICATE_UVCI_IN_REQUEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SIZE_OF_UVCI_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RevocationListDtoTest {

    private final JFixture fixture = new JFixture();

    @Test
    public void GIVEN_nullUvciList_THEN_throwsException() {
        RevocationListDto testee = new RevocationListDto(null, fixture.create(SystemSource.class));
        RevocationException exception = assertThrows(RevocationException.class, testee::validate);
        assertEquals(INVALID_SIZE_OF_UVCI_LIST, exception.getError());
    }

    @Test
    public void GIVEN_emptyUvciList_THEN_throwsException() {
        RevocationListDto testee = new RevocationListDto(new ArrayList<>(), fixture.create(SystemSource.class));
        RevocationException exception = assertThrows(RevocationException.class, testee::validate);
        assertEquals(INVALID_SIZE_OF_UVCI_LIST, exception.getError());
    }

    @Test
    public void GIVEN_tooLargeUvciList_THEN_throwsException() {
        List<UvciForRevocationDto> uvcis = new ArrayList<>();
        for (int i = 1; i <= 101; i++) {
            uvcis.add(fixture.create(UvciForRevocationDto.class));
        }

        RevocationListDto testee = new RevocationListDto(uvcis, null);
        RevocationException exception = assertThrows(RevocationException.class, testee::validate);
        assertEquals(INVALID_SIZE_OF_UVCI_LIST, exception.getError());
    }

    @Test
    public void GIVEN_UvciListWithDuplicates_THEN_throwsException() {
        String uvci = FixtureCustomization.createUVCI();
        List<UvciForRevocationDto> uvcis = new ArrayList<>();
        uvcis.add(new UvciForRevocationDto(uvci, fixture.create(Boolean.class)));
        uvcis.add(new UvciForRevocationDto(uvci, fixture.create(Boolean.class)));

        RevocationListDto testee = new RevocationListDto(uvcis, null);
        RevocationException exception = assertThrows(RevocationException.class, testee::validate);
        assertEquals(DUPLICATE_UVCI_IN_REQUEST, exception.getError());
    }
}
