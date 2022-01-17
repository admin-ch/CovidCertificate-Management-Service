package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.FixtureCustomization;
import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.response.CheckRevocationListResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.service.RevocationService;
import ch.admin.bag.covidcertificate.testutil.JeapAuthenticationTestTokenBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRevocationListDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    private KpiDataService kpiLogService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    private static final String REVOCATION_URL = "/api/v1/revocation";

    private static final String REVOCATION_LIST_CHECK_URL = "/api/v1/revocation/uvcilist/check";

    private static final String REVOCATION_LIST_URL = "/api/v1/revocation/uvcilist";

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    static void setup() {
        customizeRevocationDto(fixture);
        customizeRevocationListDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().doNothing().when(kpiLogService).saveKpiData(any());
        lenient().doNothing().when(revocationService).createRevocation(anyString());
        lenient().when(revocationService.getRevocations())
                .thenReturn(fixture.collections().createCollection(List.class, String.class));
        Jwt jwt = mock(Jwt.class);
        JeapAuthenticationToken token = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);
    }

    @Nested
    @DisplayName("POST " + REVOCATION_URL)
    class Create {

        @Test
        @DisplayName("GIVEN a uvci WHEN it is not already revoked THEN we return status 201")
        void revokeCertificateAndReturnCreatedStatus_ifNotRevokedYet() throws Exception {
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaimAsString(anyString())).thenReturn(fixture.create(String.class));
            JeapAuthenticationToken token = JeapAuthenticationTestTokenBuilder.createWithJwt(jwt).build();
            lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(token);

            var createDto = fixture.create(RevocationDto.class);
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
        @DisplayName("GIVEN a uvci WHEN it is not already revoked THEN we return code of the exception")
        void returnsStatusCodeOfRevocationException_ifOneWasThrown() throws Exception {
            var createDto = fixture.create(RevocationDto.class);
            var exception = fixture.create(RevocationException.class);
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
        @DisplayName("GIVEN an AccessDeniedException WHEN authorizing the user THEN 403 status code is returned")
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
    @DisplayName("POST " + REVOCATION_LIST_CHECK_URL)
    class CheckMassRevocation {
        @Test
        @DisplayName("GIVEN a valid uvci list THEN status 202 is returned")
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);

            mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isAccepted());
        }

        @Test
        @DisplayName("GIVEN a uvci list WHEN it contains already revoked uvcis THEN the UvcisWithError are returned")
        void returnsAlreadyRevokedUvcisAsUvcisWithError() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            var uvciToErrorMessage = fixture.collections().createMap(String.class, String.class);
            when(revocationService.getUvcisWithErrorMessage(any())).thenReturn(uvciToErrorMessage);

            MockHttpServletResponse response = mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isAccepted()).andReturn().getResponse();

            CheckRevocationListResponseDto expectedDto = mapper.readValue(response.getContentAsString(), CheckRevocationListResponseDto.class);
            assertThat(expectedDto.getUvciToErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("GIVEN a uvci list WHEN it contains not existing uvcis THEN we the UvcisWithWarning are returned")
        void returnsNotExistingUvcisAsUvcisWithWarning() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            var uvciToWarningMessage = fixture.collections().createMap(String.class, String.class);
            when(revocationService.getNotExistingUvcis(any())).thenReturn(uvciToWarningMessage);

            MockHttpServletResponse response = mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isAccepted()).andReturn().getResponse();

            CheckRevocationListResponseDto expectedDto = mapper.readValue(response.getContentAsString(), CheckRevocationListResponseDto.class);
            assertThat(expectedDto.getUvciToWarningMessage()).isEqualTo(uvciToWarningMessage);
        }

        @Test
        @DisplayName("GIVEN a uvci list WHEN some are already revoked and some are not existing THEN the revocable uvcis contain warning uvcis but no error uvcis")
        void returnsValidAndWarningUvcisAsRevocable() throws Exception {
            var testUvcis = Arrays.asList(FixtureCustomization.createUVCI(), FixtureCustomization.createUVCI(), FixtureCustomization.createUVCI());
            var createDto = new RevocationListDto(testUvcis, fixture.create(String.class));

            var errorUvcis = testUvcis.subList(0, 1).stream().collect(Collectors.toMap(s -> s, s -> "any error"));
            when(revocationService.getUvcisWithErrorMessage(any())).thenReturn(errorUvcis);
            var warningUvcis = testUvcis.subList(1, 2).stream().collect(Collectors.toMap(s -> s, s -> "any warning"));
            when(revocationService.getNotExistingUvcis(any())).thenReturn(warningUvcis);

            MockHttpServletResponse response = mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isAccepted()).andReturn().getResponse();

            CheckRevocationListResponseDto expectedDto = mapper.readValue(response.getContentAsString(), CheckRevocationListResponseDto.class);
            assertThat(expectedDto.getRevocableUvcis()).containsAll(warningUvcis.keySet());
            assertThat(expectedDto.getRevocableUvcis()).doesNotContainAnyElementsOf(errorUvcis.keySet());
        }

        @Test
        @DisplayName("GIVEN an AccessDeniedException WHEN authorizing the user THEN 403 status code is returned")
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(
                    fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(REVOCATION_LIST_CHECK_URL)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", anyString())
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    @DisplayName("POST " + REVOCATION_LIST_URL)
    class MassRevocation {
        @Test
        @DisplayName("GIVEN a uvci and uvci is not revoked WHEN uvci is called for revocation THEN we return created status")
        void revokeCertificateAndReturnCreatedStatus() throws Exception {
            var createDto = fixture.create(RevocationListDto.class);
            when(revocationService.getUvcisWithErrorMessage(anyList())).thenReturn(new HashMap<>());
            doNothing().when(revocationService).createRevocation(anyString());

            MockHttpServletResponse response = mockMvc
                    .perform(post(REVOCATION_LIST_URL)
                            .accept(MediaType.ALL_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated()).andReturn().getResponse();

            String result = response.getContentAsString();
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("GIVEN an AccessDeniedException WHEN authorizing the user THEN 403 status code is returned")
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
