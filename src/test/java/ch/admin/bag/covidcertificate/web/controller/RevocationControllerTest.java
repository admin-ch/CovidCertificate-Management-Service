package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationListDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class RevocationControllerTest {
    private static final String REVOCATION_URL = "/api/v1/revocation";
    private static final String REVOCATION_LIST_URL = "/api/v1/revocation/uvcilist";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    @InjectMocks
    private RevocationController controller;
    @Mock
    private RevocationService revocationService;
    @Mock
    private KpiDataService kpiLogService;

    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        customizeRevocationDto(fixture, false);
        customizeRevocationListDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().doNothing().when(kpiLogService).saveKpiData(any());
        lenient().doNothing().when(revocationService).createRevocation(anyString(), anyBoolean());
        lenient().when(revocationService.getRevocations())
                .thenReturn(fixture.collections().createCollection(List.class, String.class));
        Jwt jwt = mock(Jwt.class);
        JeapAuthenticationToken token = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
    }

    @Nested
    @DisplayName("POST " + REVOCATION_URL)
    class Create {

        @Test
        @DisplayName("GIVEN a uvci WHEN it is not already revoked THEN we return status 201")
        void revokeCertificateAndReturnCreatedStatus_ifNotRevokedYet() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doNothing().when(revocationService).createRevocation(anyString(), anyBoolean());

            mockMvc.perform(post(REVOCATION_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("GIVEN a uvci WHEN it is not already revoked THEN we return code of the exception")
        void returnsStatusCodeOfRevocationException_ifOneWasThrown() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            var exception = fixture.create(RevocationException.class);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doThrow(exception).when(revocationService).createRevocation(anyString(), anyBoolean());

            mockMvc.perform(post(REVOCATION_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

    }
}
