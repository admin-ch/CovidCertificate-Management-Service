package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.exception.BiDataException;
import ch.admin.bag.covidcertificate.api.response.BiDataResponseDto;
import ch.admin.bag.covidcertificate.service.BiDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static ch.admin.bag.covidcertificate.api.Constants.DATES_NOT_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.WRITING_CSV_RESULT_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class BiDataControllerTest {
    private static final String B_URL = "/api/v1/bi-data/{fromDate}/{toDate}";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    @InjectMocks
    private BiDataController controller;
    @Mock
    private BiDataService biDataService;
    private MockMvc mockMvc;

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
    }

    @Test
    void loadBiData_with_valid_dates() throws Exception {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 16);
        BiDataResponseDto biDataResponseDto = fixture.create(BiDataResponseDto.class);
        when(biDataService.loadBiData(from, to)).thenReturn(biDataResponseDto);

        String url = B_URL.replace("{fromDate}", from.format(DateTimeFormatter.ISO_DATE));
        url = url.replace("{toDate}", to.format(DateTimeFormatter.ISO_DATE));

        MvcResult result = mockMvc
                .perform(get(url).header("Authorization", fixture.create(String.class)))
                .andExpect(status().isOk())
                .andReturn();

        BiDataResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), BiDataResponseDto.class);
        assertEquals(expectedDto, biDataResponseDto);
    }

    @Test
    void loadBiData_with_invalid_dates() throws Exception {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 14);
        when(biDataService.loadBiData(from, to)).thenThrow(new BiDataException(DATES_NOT_VALID));

        String url = B_URL.replace("{fromDate}", from.format(DateTimeFormatter.ISO_DATE));
        url = url.replace("{toDate}", to.format(DateTimeFormatter.ISO_DATE));

        mockMvc.perform(get(url).header("Authorization", fixture.create(String.class)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(DATES_NOT_VALID.toString(), result.getResponse().getContentAsString()));
    }

    @Test
    void loadBiData_with_valid_dates_but_BiDataException_with_WRITING_CSV_RESULT_FAILED_error() throws Exception {
        LocalDate from = LocalDate.of(2022, 10, 10);
        LocalDate to = LocalDate.of(2022, 10, 16);
        when(biDataService.loadBiData(from, to)).thenThrow(new BiDataException(WRITING_CSV_RESULT_FAILED));

        String url = B_URL.replace("{fromDate}", from.format(DateTimeFormatter.ISO_DATE));
        url = url.replace("{toDate}", to.format(DateTimeFormatter.ISO_DATE));

        mockMvc.perform(get(url).header("Authorization", fixture.create(String.class)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertEquals(WRITING_CSV_RESULT_FAILED.toString(), result.getResponse().getContentAsString()));
    }
}
