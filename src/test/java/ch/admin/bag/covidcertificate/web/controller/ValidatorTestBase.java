package ch.admin.bag.covidcertificate.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flextrade.jfixture.JFixture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AllArgsConstructor
@Getter
class ErrorResponse {
    private List<String> errors;
}

@Setter
abstract class ValidatorTestBase {

    private MockMvc mockMvc;
    private String url;
    private JFixture fixture;
    private ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().modules(new JavaTimeModule()).build();

    void performNegativeTest(String dto, String expectedErrMsg) throws Exception {
        var req = performRequest(dto);
        req.andExpect(status().isBadRequest());
        req.andExpect(jsonPath("$.errors", Matchers.hasItem(expectedErrMsg)));
    }

    void performPositiveTest(String dto) throws Exception {
        var req = performRequest(dto);
        req.andExpect(status().isOk());
    }

    private ResultActions performRequest(String dto) throws Exception {
        var request = post(url)
                .accept(MediaType.ALL_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", fixture.create(String.class));

        if (Objects.nonNull(dto)) {
            request.content(dto);
        }
        return mockMvc.perform(request);
    }
}
