package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationListDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
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

    private static final String REVOCATION_URL = "/api/v1/revocation";

    private static final String REVOCATION_LIST_CHECK_URL = "/api/v1/revocation/uvcilist/check";

    private static final String REVOCATION_LIST_URL = "/api/v1/revocation/uvcilist/revoke";

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    static void setup() {
        customizeRevocationDto(fixture);
        customizeRevocationListDto(fixture);
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
    class Create {
        @Test
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doNothing().when(revocationService).createRevocation(anyString());

            mockMvc.perform(post(REVOCATION_URL)
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
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doThrow(exception).when(revocationService).createRevocation(anyString());

            mockMvc.perform(post(REVOCATION_URL)
                                    .accept(MediaType.ALL_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(
                    fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(REVOCATION_URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    class CheckList {
        @Test
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doNothing().when(revocationService).createRevocation(anyString());

            mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                                    .accept(MediaType.ALL_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().isCreated());

        }

        @Test
        void returnsStatusCodeOfRevocationException_ifOneWasThrown() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            var exception = fixture.create(RevocationException.class);
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doThrow(exception).when(revocationService).createRevocation(anyString());

            mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                                    .accept(MediaType.ALL_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(
                    fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    class CreateList {
        @Test
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doNothing().when(revocationService).createRevocation(anyString());

            MockHttpServletResponse response = mockMvc
                    .perform(post(REVOCATION_LIST_URL)
                                     .accept(MediaType.ALL_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE)
                                     .header("Authorization", fixture.create(String.class))
                                     .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk()).andReturn().getResponse();
            String result = response.getContentAsString();
            assertThat(result).isNotNull();
        }

        @Test
        void returnsStatusCodeOfRevocationException_ifOneWasThrown() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            var exception = fixture.create(RevocationException.class);
            when(revocationService.doesUvciExist(anyString())).thenReturn(true);
            when(revocationService.isAlreadyRevoked(anyString())).thenReturn(false);
            doThrow(exception).when(revocationService).createRevocation(anyString());

            mockMvc.perform(post(REVOCATION_LIST_URL)
                                    .accept(MediaType.ALL_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(
                    fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(REVOCATION_LIST_URL)
                                    .accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                   .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }
}
