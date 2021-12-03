package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.domain.Revocation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RevocationMapperTest {
    private final JFixture jFixture = new JFixture();

    @Test
    void whenToRevocation_thenMapsUVCI() {
        //given
        String uvci = jFixture.create(String.class);
        // when
        Revocation result = RevocationMapper.toRevocation(uvci);
        // then
        assertEquals(uvci, result.getUvci());
    }
}
