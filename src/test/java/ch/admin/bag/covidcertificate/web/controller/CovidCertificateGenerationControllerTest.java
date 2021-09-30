package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.RecoveryCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.TestCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificatePdfGenerateRequestDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.CovidCertificateGenerationService;
import ch.admin.bag.covidcertificate.service.CovidCertificateVaccinationValidationService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static ch.admin.bag.covidcertificate.FixtureCustomization.*;
import static ch.admin.bag.covidcertificate.TestModelProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CovidCertificateGenerationControllerTest {
    @InjectMocks
    private CovidCertificateGenerationController controller;
    @Mock
    private SecurityHelper securityHelper;
    @Mock
    private CovidCertificateGenerationService covidCertificateGenerationService;
    @Mock
    private ServletJeapAuthorization jeapAuthorization;
    @Mock
    private KpiDataService kpiLogService;
    @Mock
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    private static final String BASE_URL = "/api/v1/covidcertificate/";

    private static final JFixture fixture = new JFixture();

    @BeforeAll
    static void setup() {
        customizeVaccinationCertificateCreateDto(fixture);
        customizeTestCertificateCreateDto(fixture);
        customizeRecoveryCertificateCreateDto(fixture);
        customizeCreateCertificateException(fixture);
        customizeCovidCertificateCreateResponseDto(fixture);
    }

    @BeforeEach
    void setupMocks() throws IOException {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(fixture.create(CovidCertificateCreateResponseDto.class));
        lenient().when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Nested
    class CreateVaccinationCertificate {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(any(VaccinationCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    class CertificateCreateAddress {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returns400StatusCode_ifInvalidZipCode() throws Exception {
            var createDto = getVaccinationCertificateCreateDto("EU/1/20/1507", "de");
            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 0);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 999);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 10000);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        void returns400StatusCode_ifInvalidCity() throws Exception {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            customizeCovidCertificateAddressDto(fixture, createDto, "city", null);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }

        @Test
        void returns400StatusCode_ifInvalidLine1() throws Exception {
            var createDto = fixture.create(VaccinationCertificateCreateDto.class);
            customizeCovidCertificateAddressDto(fixture, createDto, "streetAndNr", null);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        }
    }

    @Nested
    class CreateTestCertificate {
        private static final String URL = BASE_URL + "test";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            lenient().when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(any(TestCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = getTestCertificateCreateDto(null, "1833", "de");
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    class CreateRecoveryCertificate {
        private static final String URL = BASE_URL + "recovery";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(any(RecoveryCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns403StatusCode_ifAccessDeniedExceptionWasThrown() throws Exception {
            var createDto = fixture.create(RecoveryCertificateCreateDto.class);
            when(securityHelper.authorizeUser(any(HttpServletRequest.class))).thenThrow(fixture.create(AccessDeniedException.class));

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Nested
    class GenerateVaccinationPdfFromExistingCertificate {
        private static final String URL = BASE_URL + "fromexisting/vaccination";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture.create(VaccinationCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(VaccinationCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
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
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture.create(TestCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(TestCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
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
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class))).thenReturn(responseDto);

            MvcResult result = mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var pdfGenerateRequestDto = fixture.create(RecoveryCertificatePdfGenerateRequestDto.class);
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateFromExistingCovidCertificate(any(RecoveryCertificatePdfGenerateRequestDto.class))).thenThrow(exception);

            mockMvc.perform(post(URL)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header("Authorization", fixture.create(String.class))
                    .content(mapper.writeValueAsString(pdfGenerateRequestDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }
    }
}
