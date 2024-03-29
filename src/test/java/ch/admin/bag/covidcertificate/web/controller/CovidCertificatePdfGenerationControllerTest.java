package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.AntibodyCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.ExceptionalCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryRatCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.domain.enums.Delivery;
import ch.admin.bag.covidcertificate.service.CovidCertificateGeneratePdfFromExistingService;
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

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCovidCertificateCreateResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CovidCertificatePdfGenerationControllerTest {

    private static final String BASE_URL = "/api/v1/covidcertificate/";
    private static final JFixture fixture = new JFixture();

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    @InjectMocks
    private CovidCertificateGeneratePdfFromExistingController controller;

    @Mock
    private CovidCertificateGeneratePdfFromExistingService covidCertificateGeneratePdfFromExistingService;

    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        customizeCovidCertificateCreateResponseDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(
                fixture.create(JeapAuthenticationToken.class));
    }

    @Nested
    class GenerateVaccinationPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/vaccination";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(VaccinationCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier", Delivery.OTHER);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(VaccinationCertificatePdfGenerateRequestDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                                      .andExpect(status().isOk())
                                      .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(VaccinationCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(VaccinationCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateVaccinationTouristPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/vaccination-tourist";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier", Delivery.OTHER);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(VaccinationTouristCertificatePdfGenerateRequestDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(VaccinationTouristCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(VaccinationTouristCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateTestPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/test";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(TestCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier", Delivery.OTHER);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(TestCertificatePdfGenerateRequestDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                                      .andExpect(status().isOk())
                                      .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(TestCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(TestCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateRecoveryPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/recovery";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(RecoveryCertificatePdfGenerateRequestDto.class);
            var expectedDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var expectedEnvelope = fixture.create(CovidCertificateResponseEnvelope.class);
            expectedEnvelope.setResponseDto(expectedDto);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(RecoveryCertificatePdfGenerateRequestDto.class))).thenReturn(expectedEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                                      .andExpect(status().isOk())
                                      .andReturn();

            CovidCertificateCreateResponseDto resultDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(expectedDto, resultDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(RecoveryCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(RecoveryCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                             .contentType(MediaType.APPLICATION_JSON_VALUE)
                             .header("Authorization", fixture.create(String.class))
                             .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                   .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateRecoveryRatPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/recovery-rat";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var expectedDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var expectedEnvelope = fixture.create(CovidCertificateResponseEnvelope.class);
            expectedEnvelope.setResponseDto(expectedDto);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(RecoveryRatCertificatePdfGenerateRequestDto.class))).thenReturn(expectedEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto resultDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(expectedDto, resultDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(RecoveryRatCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(RecoveryRatCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateAntibodyPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/antibody";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(AntibodyCertificatePdfGenerateRequestDto.class);
            var expectedDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var expectedEnvelope = fixture.create(CovidCertificateResponseEnvelope.class);
            expectedEnvelope.setResponseDto(expectedDto);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(AntibodyCertificatePdfGenerateRequestDto.class))).thenReturn(expectedEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto resultDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(expectedDto, resultDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(AntibodyCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(AntibodyCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }

    @Nested
    class GenerateExceptionalPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/exceptional";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var expectedDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var expectedEnvelope = fixture.create(CovidCertificateResponseEnvelope.class);
            expectedEnvelope.setResponseDto(expectedDto);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(ExceptionalCertificatePdfGenerateRequestDto.class))).thenReturn(expectedEnvelope);

            MvcResult result = mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto resultDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(expectedDto, resultDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture
                    .create(ExceptionalCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGeneratePdfFromExistingService.generateFromExistingCovidCertificate(
                    any(ExceptionalCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(
                    post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("Authorization", fixture.create(String.class))
                            .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }
}
