package ch.admin.bag.covidcertificate.config.security.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@DisplayName("Simple test for the basics of the JeapAuthenticationToken")
class JeapAuthenticationTokenTest {

    private JeapAuthenticationToken testEe;

    @Mock
    private Jwt token;

    @BeforeEach
    void setUpTestEe() {
        Set<String> roles = Set.of("one", "two", "three");
        testEe = new JeapAuthenticationToken(token, roles);
    }

    @Test
    void testToString() {
        assertThat(this.testEe).isNotNull();
        assertThat(this.testEe.toString()).startsWith("JeapAuthenticationToken{ subject (calling user):");
    }

    @Test
    void testEquals_true() {
        assertThat(this.testEe).isNotNull().isEqualTo(this.testEe);
    }

    @Test
    void testEquals_false() {
        assertThat(this.testEe).isNotNull();
        Set<String> roles = Set.of("four", "five", "six");
        JeapAuthenticationToken otherTestEe = new JeapAuthenticationToken(token, roles);
        assertThat(this.testEe.equals(otherTestEe)).isFalse();
    }

    @Test
    void testHashCode_hasToBeDifferent() {
        assertThat(this.testEe).isNotNull();
        Set<String> roles = Set.of("four", "five", "six");
        JeapAuthenticationToken otherTestEe = new JeapAuthenticationToken(token, roles);
        assertThat(this.testEe.hashCode()).isNotEqualTo(otherTestEe.hashCode());
    }
}