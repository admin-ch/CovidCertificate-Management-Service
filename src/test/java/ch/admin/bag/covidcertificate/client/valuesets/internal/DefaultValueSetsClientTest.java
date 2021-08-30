package ch.admin.bag.covidcertificate.client.valuesets.internal;

import ch.admin.bag.covidcertificate.client.valuesets.dto.ValueSetDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultValueSetsClientTest {

    static MockWebServer mockWebServer;

    private DefaultValueSetsClient client;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        this.client = new DefaultValueSetsClient(WebClient.create());
        ReflectionTestUtils.setField(this.client, "serviceUri", String.format("http://localhost:%s/", mockWebServer.getPort()));
    }

    @Test
    void doesGetSuccessfully() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody("{\"valueSetId\":\"covid-19-lab-test-manufacturer-and-name\",\"valueSetDate\":\"2021-08-30\",\"valueSetValues\":{\"1341\":{\"display\":\"Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)\",\"lang\":\"en\",\"active\":true,\"system\":\"https://covid-19-diagnostics.jrc.ec.europa.eu/devices\",\"version\":\"2021-07-07 05:23:59 CEST\"},\"1065\":{\"display\":\"Becton Dickinson, BD Veritor? System for Rapid Detection of SARS CoV 2\",\"lang\":\"en\",\"active\":true,\"system\":\"https://covid-19-diagnostics.jrc.ec.europa.eu/devices\",\"version\":\"2021-07-07 05:13:00 CEST\"}}}"));
        Map<String, ValueSetDto> response = assertDoesNotThrow(() -> this.client.getValueSets("covid-19-lab-test-manufacturer-and-name"));
        Assertions.assertThat(response).hasSize(2);
    }

    @Test
    void throwsException__ifResponseCode500() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        assertThrows(IllegalStateException.class, () -> this.client.getValueSets("covid-19-lab-test-manufacturer-and-name"));
    }

    @Test
    void throwsException__ifServiceUnreachable() {
        ReflectionTestUtils.setField(this.client, "serviceUri", "http://127.0.0.1");
        assertThrows(IllegalStateException.class, () -> this.client.getValueSets("covid-19-lab-test-manufacturer-and-name"));
    }


    @AfterAll
    static void tearDown() throws Throwable {
        mockWebServer.shutdown();
    }
}
