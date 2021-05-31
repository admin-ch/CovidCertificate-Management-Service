package ch.admin.bag.covidcertificate.api.mapper;

import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.domain.Revocation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RevocationMapperTest {
    private final JFixture jFixture = new JFixture();

    @Test
    public void whenToRevocation_thenMapsUVCI() {
        //given
        RevocationDto revocationDto = jFixture.create(RevocationDto.class);
        // when
        Revocation result = RevocationMapper.toRevocation(revocationDto);
        // then
        assertEquals(revocationDto.getUvci(), result.getUvci());
    }
}
