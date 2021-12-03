package ch.admin.bag.covidcertificate.web.controller;

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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class RevocationListControllerTest {
    @InjectMocks
    private RevocationListController controller;
    @Mock
    private RevocationService revocationService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    private static final String URL = "/api/v1/revocation-list";

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    static void setup() {
        customizeRevocationDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(revocationService.getRevocations())
                 .thenReturn(fixture.collections().createCollection(List.class, String.class));
        lenient().doNothing().when(revocationService).createRevocation(anyString());
        lenient().when(jeapAuthorization.getJeapAuthenticationToken())
                 .thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Nested
    class Get {
        @Test
        void returnsRevokedCertificateIdsWithOkStatus() throws Exception {
            var responseDto = fixture.collections().createCollection(List.class, String.class);
            when(revocationService.getRevocations()).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(get(URL)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class)))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(mapper.writeValueAsString(responseDto), result.getResponse().getContentAsString());
        }
    }

}
