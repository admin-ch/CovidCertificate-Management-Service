package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.mapper.ValueSetsResponseDtoMapper;
import ch.admin.bag.covidcertificate.api.response.ValueSetsResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.service.ValueSetsService;
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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeIssuableVaccineDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ValueSetsControllerTest {
    private static final String URL = "/api/v1/valuesets";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    @InjectMocks
    private ValueSetsController controller;
    @Mock
    private ValueSetsService valueSetsService;
    private MockMvc mockMvc;

    @BeforeAll
    static void setup() {
        customizeIssuableVaccineDto(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);
    }

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
        lenient().when(valueSetsService.getValueSets()).thenReturn(fixture.create(ValueSetsDto.class));
    }

    @Nested
    class Get {
        @Test
        void mapsValueSetsToValueSetResponseDto() throws Exception {
            var responseDto = fixture.create(ValueSetsDto.class);
            when(valueSetsService.getValueSets()).thenReturn(responseDto);

            try (MockedStatic<ValueSetsResponseDtoMapper> valueSetsResponseDtoMapperMock = Mockito.mockStatic(ValueSetsResponseDtoMapper.class)) {
                valueSetsResponseDtoMapperMock
                        .when(() -> ValueSetsResponseDtoMapper.create(any()))
                        .thenReturn(fixture.create(ValueSetsResponseDto.class));

                mockMvc.perform(get(URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", fixture.create(String.class)));

                valueSetsResponseDtoMapperMock.verify(() -> ValueSetsResponseDtoMapper.create(responseDto));
            }
        }

        @Test
        void returnsValueSetsWithOkStatus() throws Exception {
            var valueSetResponseDto = fixture.create(ValueSetsResponseDto.class);
            try (MockedStatic<ValueSetsResponseDtoMapper> valueSetsResponseDtoMapperMock = Mockito.mockStatic(ValueSetsResponseDtoMapper.class)) {
                valueSetsResponseDtoMapperMock
                        .when(() -> ValueSetsResponseDtoMapper.create(any()))
                        .thenReturn(valueSetResponseDto);

                MvcResult result = mockMvc.perform(get(URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", fixture.create(String.class)))
                        .andExpect(status().isOk())
                        .andReturn();

                assertEquals(mapper.writeValueAsString(valueSetResponseDto), result.getResponse().getContentAsString());
            }
        }
    }

}