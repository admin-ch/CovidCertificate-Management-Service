package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultVaccineValueSetsClientTest {

    static MockWebServer mockWebServer;

    private DefaultVaccineValueSetsClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        this.client = new DefaultVaccineValueSetsClient(HttpClient.newHttpClient(), new ObjectMapper());
        ReflectionTestUtils.setField(this.client, "vaccineBaseUrl", String.format("http://localhost:%s/", mockWebServer.getPort()));
        ReflectionTestUtils.setField(this.client, "authHolderBaseUrl", String.format("http://localhost:%s/", mockWebServer.getPort()));
        ReflectionTestUtils.setField(this.client, "prophylaxisBaseUrl", String.format("http://localhost:%s/", mockWebServer.getPort()));
        ReflectionTestUtils.setField(this.client, "readTimeout", 8);
    }

    @AfterAll
    static void tearDown() throws Throwable {
        mockWebServer.shutdown();
    }

    @Test
    void doesGetVaccineValueSetSuccessfully() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody("{\"valueSetId\":\"vaccines-covid-19-names\",\"valueSetDate\":\"2022-01-26\",\"valueSetValues\":{\"EU/1/20/1528\":{\"display\":\"Comirnaty\",\"lang\":\"en\",\"active\":true,\"system\":\"https://ec.europa.eu/health/documents/community-register/html/\",\"version\":\"\"}}}"));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, VaccineValueSetDto> result = this.client.getVaccineValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("EU/1/20/1528").getDisplay()).isEqualTo("Comirnaty");
    }

    @Test
    void doesGetAuthHolderValueSetSuccessfully() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody("{\"valueSetId\":\"vaccines-covid-19-auth-holders\",\"valueSetDate\":\"2022-01-26\",\"valueSetValues\":{\"ORG-100001699\":{\"display\":\"AstraZeneca AB\",\"lang\":\"en\",\"active\":true,\"system\":\"https://spor.ema.europa.eu/v1/organisations\",\"version\":\"\"}}}"));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, AuthHolderValueSetDto> result = this.client.getAuthHolderValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("ORG-100001699").getDisplay()).isEqualTo("AstraZeneca AB");
    }

    @Test
    void DoesGetProphylaxisValueSetSuccessfully() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setBody("{\"valueSetId\":\"sct-vaccines-covid-19\",\"valueSetDate\":\"2021-04-27\",\"valueSetValues\":{\"1119349007\":{\"display\":\"SARS-CoV-2 mRNA vaccine\",\"lang\":\"en\",\"active\":true,\"version\":\"http://snomed.info/sct/900000000000207008/version/20210131\",\"system\":\"http://snomed.info/sct\"}}}"));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, ProphylaxisValueSetDto> result = this.client.getProphylaxisValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("1119349007").getDisplay()).isEqualTo("SARS-CoV-2 mRNA vaccine");
    }

    @Test
    void throwsIllegalStateException_getVaccineValueSet_ifResponseCode500() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getVaccineValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getAuthHolderValueSet_ifResponseCode500() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getAuthHolderValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getProphylaxisValueSet_ifResponseCode500() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getProphylaxisValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getVaccineValueSet_ifURIArgumentIsIllegal() {
        ReflectionTestUtils.setField(this.client, "vaccineBaseUrl", "IllegalArgument");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getVaccineValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getAuthHolderValueSet_ifURIArgumentIsIllegal() {
        ReflectionTestUtils.setField(this.client, "authHolderBaseUrl", "IllegalArgument");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getAuthHolderValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getProphylaxisValueSet_ifURIArgumentIsIllegal() {
        ReflectionTestUtils.setField(this.client, "prophylaxisBaseUrl", "IllegalArgument");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getProphylaxisValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getVaccineValueSet_ifURISyntaxIsWrong() {
        ReflectionTestUtils.setField(this.client, "vaccineBaseUrl", "http:\\\\localhost:%s/");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getVaccineValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getAuthHolderValueSet_ifURISyntaxIsWrong() {
        ReflectionTestUtils.setField(this.client, "authHolderBaseUrl", "http:\\\\localhost:%s/");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getAuthHolderValueSet(importControl));
    }

    @Test
    void throwsIllegalStateException_getProphylaxisValueSet_ifURISyntaxIsWrong() {
        ReflectionTestUtils.setField(this.client, "prophylaxisBaseUrl", "http:\\\\localhost:%s/");
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        assertThrows(IllegalStateException.class, () -> this.client.getProphylaxisValueSet(importControl));
    }
}