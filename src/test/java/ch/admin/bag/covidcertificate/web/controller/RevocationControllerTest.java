package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.RevocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class RevocationControllerTest {
    @InjectMocks
    private RevocationController controller;
    @Mock
    private SecurityHelper securityHelper;
    @Mock
    private RevocationService revocationService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    private static final String URL = "/api/v1/revocation";

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    static void setup() {
        customizeRevocationDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(revocationService.getRevocations()).thenReturn(fixture.collections().createCollection(List.class, String.class));
        lenient().doNothing().when(revocationService).createRevocation(any(RevocationDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Nested
    class Create {
        @Test
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            doNothing().when(revocationService).createRevocation(any(RevocationDto.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated());

        }

        @Test
        void returnsStatusCodeOfRevocationException_ifOneWasThrown() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            var exception = fixture.create(RevocationException.class);
            doThrow(exception).when(revocationService).createRevocation(any(RevocationDto.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.ALL_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }
}
