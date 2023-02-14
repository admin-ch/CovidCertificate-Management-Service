package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.authorization.AuthorizationInterceptor;
import ch.admin.bag.covidcertificate.config.security.OAuth2SecuredWebConfiguration;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CovidCertificateConversionService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;

import static ch.admin.bag.covidcertificate.FixtureCustomization.*;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateConversionRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CovidCertificateConversionControllerTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/conversion/";
    private static final JFixture fixture = new JFixture();

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    @RunWith(SpringRunner.class)
    @WebMvcTest(value = {CovidCertificateConversionController.class})
    @AutoConfigureMockMvc(addFilters = false)
    @Nested
    class CovidCertificateConversionControllerValidationTest {
        @MockBean
        private AuthorizationInterceptor authorizationInterceptor;
        @MockBean
        private CovidCertificateConversionService covidCertificateConversionService;
        @MockBean
        private OAuth2SecuredWebConfiguration.OAuth2SecuredWebMvcConfiguration oAuth2SecuredWebMvcConfiguration;
        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private KpiDataService kpiDataService;

        private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

        @BeforeEach
        void beforeEach() {
            when(authorizationInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        }

        @Nested
        @DisplayName("/vaccination")
        class VaccinationTests {
            private static final String VACCINATION_URL = BASE_URL + "vaccination";

            @ParameterizedTest
            @CsvFileSource(resources = "/csv/conversion_controller_request_validation.csv", delimiter = 'þ')
            void InvalidRequestBodyTest(String dto, String expectedErrMsg) throws Exception {
                // given
                var request = post(VACCINATION_URL)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", fixture.create(String.class));

                if (Objects.nonNull(dto)) {
                    request.content(dto);
                }

                // when then
                mockMvc.perform(request)
                        .andExpect(status().isBadRequest())
                        .andExpect(content().string(Matchers.containsString(expectedErrMsg)));

            }
        }
    }

    @ExtendWith(MockitoExtension.class)
    @Nested
    class CovidCertificateConversionControllerUnitTest {

        @InjectMocks
        private CovidCertificateConversionController controller;

        @Mock
        private CovidCertificateConversionService covidCertificateConversionService;

        @Mock
        private ServletJeapAuthorization jeapAuthorization;

        @Mock
        private KpiDataService kpiLogService;

        private MockMvc mockMvc;

        @BeforeAll
        static void setup() {
            customizeVaccinationCertificateCreateDto(fixture);
        }

        @BeforeEach
        void setupMocks() {
            this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
            lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(
                    fixture.create(JeapAuthenticationToken.class));
        }

        @Nested
        class ConvertVaccinationCertificate {
            private static final String URL = BASE_URL + "vaccination";

            @Test
            void returnsCertificateWithOkStatus() throws Exception {
                var conversionDto = getVaccinationCertificateConversionRequestDto();
                var responseDto = fixture.create(ConvertedCertificateResponseDto.class);
                var responseEnvelope = new ConvertedCertificateResponseEnvelope(responseDto, "someIdentifier");
                when(covidCertificateConversionService.convertFromExistingCovidCertificate(
                        any(VaccinationCertificateConversionRequestDto.class))).thenReturn(responseEnvelope);

                MvcResult result = mockMvc.perform(
                                post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .header("Authorization", fixture.create(String.class))
                                        .content(mapper.writeValueAsString(conversionDto)))
                        .andExpect(status().isOk())
                        .andReturn();

                ConvertedCertificateResponseDto expectedDto = mapper.readValue(
                        result.getResponse().getContentAsString(),
                        ConvertedCertificateResponseDto.class);
                assertEquals(responseDto, expectedDto);
            }

            @Test
            void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
                var createDto = getVaccinationCertificateConversionRequestDto();
                var exception = fixture.create(CreateCertificateException.class);
                when(covidCertificateConversionService.convertFromExistingCovidCertificate(
                        any(VaccinationCertificateConversionRequestDto.class))).thenThrow(exception);

                mockMvc.perform(
                                post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .header("Authorization", fixture.create(String.class))
                                        .content(mapper.writeValueAsString(createDto)))
                        .andExpect(status().is(exception.getError().getHttpStatus().value()));
            }
        }
    }
}
