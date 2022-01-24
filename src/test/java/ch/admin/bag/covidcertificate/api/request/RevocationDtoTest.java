package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RevocationDtoTest {

    private final JFixture fixture = new JFixture();

    @Test
    void whenValidate_thenOk() {
        //given
        String uvci = "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53E";
        RevocationDto revocationDto = new RevocationDto(uvci, fixture.create(SystemSource.class), null);
        // when
        revocationDto.validate();
        // then
        assertEquals(uvci, revocationDto.getUvci());
    }

    @ParameterizedTest
    @ValueSource(strings = {"urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53",
            "urn:uvci:01:CH:97DAB5E31B589AF3CAE2F53EE",
            "11:CH:97DAB5E31B589AF3CAE2F53E",
            "urn:uvci:01:CH:97ÜAB5E31B589AF3CAE2F53E",
            "urn:uvci:01:CH:97uAB5E31B589AF3CAE2F53E",
            "urn:uvci:01:CH:97,AB5E31B589AF3CAE2F53E"})
    void givenUVCIHasInvalidFormat_whenValidate_thenThrowsRevocationException(String uvci) {
        // given to short, to long, invalid start, invalid character, lowercase character, special character.
        // when
        RevocationDto revocationDto = new RevocationDto(uvci, fixture.create(SystemSource.class), null);
        // then
        RevocationException exception = assertThrows(RevocationException.class, revocationDto::validate);
        assertEquals(INVALID_UVCI, exception.getError());
    }
}
