package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseDto;
import ch.admin.bag.covidcertificate.api.response.ConvertedCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CovidCertificateConversionService;
import ch.admin.bag.covidcertificate.service.KpiDataService;
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

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateConversionRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CovidCertificateConversionControllerTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/conversion/";
    private static final JFixture fixture = new JFixture();

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

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
