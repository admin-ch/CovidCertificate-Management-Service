package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateResponseEnvelope;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCovidCertificateAddressDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCovidCertificateCreateResponseDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCreateCertificateException;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeExceptionalCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRecoveryRatCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccinationTouristCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getAntibodyCertificateCreateJSONWithInvalidPositiveTestResultDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getExceptionalCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getExceptionalCertificateCreateDtoJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getExceptionalCertificateCreateDtoJSONWithInvalidSampleDateTime;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryCertificateCreateJSONWithInvalidPositiveTestResultDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryRatCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryRatCertificateCreateJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getRecoveryRatCertificateCreateJSONWithInvalidPositiveTestResultDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDtoJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getTestCertificateCreateDtoJSONWithInvalidSampleDateTime;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateCreateDto;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationCertificateJSONWithInvalidVaccinationDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateCreateDtoWithoutAddress;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateJSONWithInvalidBirthdateSampleDate;
import static ch.admin.bag.covidcertificate.TestModelProvider.getVaccinationTouristCertificateJSONWithInvalidVaccinationDate;
import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_EXCEPTIONAL_VALID_FROM_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CovidCertificateGenerationControllerTest {

    @InjectMocks
    private CovidCertificateGenerationController controller;

    @Mock
    private CovidCertificateGenerationService covidCertificateGenerationService;

    @Mock
    private ServletJeapAuthorization jeapAuthorization;

    @Mock
    private KpiDataService kpiLogService;

    @Mock
    private CovidCertificateVaccinationValidationService covidCertificateVaccinationValidationService;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/api/v1/covidcertificate/";

    private static final JFixture fixture = new JFixture();

    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    @BeforeAll
    static void setup() {
        customizeVaccinationCertificateCreateDto(fixture);
        customizeVaccinationTouristCertificateCreateDto(fixture);
        customizeTestCertificateCreateDto(fixture);
        customizeRecoveryCertificateCreateDto(fixture);
        customizeRecoveryRatCertificateCreateDto(fixture);
        customizeAntibodyCertificateCreateDto(fixture);
        customizeExceptionalCertificateCreateDto(fixture);

        customizeCreateCertificateException(fixture);
        customizeCovidCertificateCreateResponseDto(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(jeapAuthorization.getJeapAuthenticationToken())
                .thenReturn(fixture.create(JeapAuthenticationToken.class));
    }

    @Nested
    class CreateVaccinationCertificate {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(VaccinationCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(VaccinationCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns465StatusCode_ifVaccinationDateIsInvalid() throws Exception {
            var JSON = getVaccinationCertificateJSONWithInvalidVaccinationDate();

            var errorCode = new CreateCertificateException(INVALID_VACCINATION_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getVaccinationCertificateJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CreateVaccinationTouristCertificate {
        private static final String URL = BASE_URL + "vaccination-tourist";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getVaccinationTouristCertificateCreateDtoWithoutAddress(
                    "EU/1/20/1507",
                    "de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(VaccinationTouristCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getVaccinationTouristCertificateCreateDtoWithoutAddress(
                    "EU/1/20/1507",
                    "de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(VaccinationTouristCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }

        @Test
        void returns465StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getVaccinationTouristCertificateJSONWithInvalidVaccinationDate();

            var errorCode = new CreateCertificateException(INVALID_VACCINATION_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getVaccinationTouristCertificateJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CertificateCreateAddress {
        private static final String URL = BASE_URL + "vaccination";

        @Test
        void returns400StatusCode_ifInvalidZipCode() throws Exception {
            var createDto = getVaccinationCertificateCreateDto(
                    "EU/1/20/1507",
                    "de");
            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 0);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 999);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            customizeCovidCertificateAddressDto(fixture, createDto, "zipCode", 10000);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
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

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
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

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
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
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            lenient().when(covidCertificateGenerationService.generateCovidCertificate(
                    any(TestCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getTestCertificateCreateDto(
                    null,
                    "1833",
                    "de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(TestCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }


        @Test
        void returns464StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getTestCertificateCreateDtoJSONWithInvalidSampleDateTime();

            var errorCode = new CreateCertificateException(INVALID_SAMPLE_DATE_TIME).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getTestCertificateCreateDtoJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CreateRecoveryCertificate {
        private static final String URL = BASE_URL + "recovery";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(RecoveryCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getRecoveryCertificateCreateDto("de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(RecoveryCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }


        @Test
        void returns466StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getRecoveryCertificateCreateJSONWithInvalidPositiveTestResultDate();

            var errorCode = new CreateCertificateException(INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT).getError()
                    .getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getRecoveryCertificateCreateJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CreateRecoveryRatCertificate {
        private static final String URL = BASE_URL + "recovery-rat";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(RecoveryRatCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getRecoveryRatCertificateCreateDto("de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(RecoveryRatCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }


        @Test
        void returns466StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getRecoveryRatCertificateCreateJSONWithInvalidPositiveTestResultDate();

            var errorCode = new CreateCertificateException(INVALID_SAMPLE_DATE_TIME).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getRecoveryRatCertificateCreateJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CreateAntibodyCertificate {
        private static final String URL = BASE_URL + "antibody";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getAntibodyCertificateCreateDto("de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(AntibodyCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getAntibodyCertificateCreateDto("de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(AntibodyCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }


        @Test
        void returns466StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getAntibodyCertificateCreateJSONWithInvalidPositiveTestResultDate();

            var errorCode = new CreateCertificateException(
                    INVALID_DATE_OF_FIRST_POSITIVE_TEST_RESULT).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getAntibodyCertificateCreateJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }

    @Nested
    class CreateExceptionalCertificate {
        private static final String URL = BASE_URL + "exceptional";

        @Test
        void returnsCertificateWithOkStatus() throws Exception {
            var createDto = getExceptionalCertificateCreateDto("de");
            var responseDto = fixture.create(CovidCertificateCreateResponseDto.class);
            var responseEnvelope = new CovidCertificateResponseEnvelope(responseDto, "someIdentifier");
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(ExceptionalCertificateCreateDto.class))).thenReturn(responseEnvelope);

            MvcResult result = mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            CovidCertificateCreateResponseDto expectedDto = mapper.readValue(
                    result.getResponse().getContentAsString(),
                    CovidCertificateCreateResponseDto.class);
            assertEquals(responseDto, expectedDto);
        }

        @Test
        void returnsStatusCodeOfCreateCertificateException_ifOneWasThrown() throws Exception {
            var createDto = getExceptionalCertificateCreateDto("de");
            var exception = fixture.create(CreateCertificateException.class);
            when(covidCertificateGenerationService.generateCovidCertificate(
                    any(ExceptionalCertificateCreateDto.class))).thenThrow(exception);

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(mapper.writeValueAsString(createDto)))
                    .andExpect(status().is(exception.getError().getHttpStatus().value()));
        }


        @Test
        void returns466StatusCode_ifDateParsingFailed() throws Exception {
            var JSON = getExceptionalCertificateCreateDtoJSONWithInvalidSampleDateTime();

            var errorCode = new CreateCertificateException(
                    INVALID_EXCEPTIONAL_VALID_FROM_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }

        @Test
        void returns453StatusCode_ifBirthdateAfterSampleDate() throws Exception {
            var JSON = getExceptionalCertificateCreateDtoJSONWithInvalidBirthdateSampleDate();
            var errorCode = new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE).getError().getErrorCode();

            mockMvc.perform(
                            post(URL).accept(MediaType.APPLICATION_JSON_VALUE)
                                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                                    .header("Authorization", fixture.create(String.class))
                                    .content(JSON))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.errorCode").value(errorCode));
        }
    }
}
