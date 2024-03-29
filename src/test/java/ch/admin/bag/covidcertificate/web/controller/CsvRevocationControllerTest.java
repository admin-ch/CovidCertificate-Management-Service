package ch.admin.bag.covidcertificate.web.controller;

import ch.admin.bag.covidcertificate.api.request.CertificateType;
import ch.admin.bag.covidcertificate.api.response.CsvRevocationResponseDto;
import ch.admin.bag.covidcertificate.service.CsvRevocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import static ch.admin.bag.covidcertificate.api.Constants.NOT_A_CSV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CsvRevocationControllerTest {
    private static final String B_URL = "/api/v1/revocation/csv";
    private static final JFixture fixture = new JFixture();
    private final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();
    @InjectMocks
    private CsvRevocationController controller;
    @Mock
    private CsvRevocationService csvRevocationService;
    private MockMvc mockMvc;

    @BeforeEach
    void setupMocks() {
        this.mockMvc = standaloneSetup(controller, new ResponseStatusExceptionHandler()).build();
    }

    @Test
    void createWithCsvWithOkStatus() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/csv",
                "Hello, World!".getBytes()
        );
        CsvRevocationResponseDto csvResponseDto = fixture.create(CsvRevocationResponseDto.class);
        when(csvRevocationService.handleCsvRequest(any(MultipartFile.class))).thenReturn(csvResponseDto);

        MvcResult result = mockMvc
                .perform(multipart(B_URL)
                        .file(file)
                        .header("Authorization", fixture.create(String.class))
                        .param("certificateType", CertificateType.RECOVERY.name()))
                .andExpect(status().isOk())
                .andReturn();

        CsvRevocationResponseDto expectedDto = mapper.readValue(result.getResponse().getContentAsString(), CsvRevocationResponseDto.class);
        assertEquals(expectedDto, csvResponseDto);
    }

    @Test
    void notACsvExceptionTest() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart(B_URL)
                        .file(file)
                        .header("Authorization", fixture.create(String.class))
                        .param("certificateType", CertificateType.RECOVERY.name()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> assertEquals(NOT_A_CSV.toString(), result.getResponse().getContentAsString()));
    }
}
