package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import ch.admin.bag.covidcertificate.client.valuesets.dto.AuthHolderValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.ProphylaxisValueSetDto;
import ch.admin.bag.covidcertificate.client.valuesets.dto.VaccineValueSetDto;
import ch.admin.bag.covidcertificate.domain.VaccineImportControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

public class DefaultVaccineValueSetsClientExceptionTest {
    static MockWebServer mockWebServer;

    private HttpClient mockedHttpClient;

    private DefaultVaccineValueSetsClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @BeforeEach
    void initialize() {
        mockedHttpClient = Mockito.mock(HttpClient.class);
        this.client = new DefaultVaccineValueSetsClient(mockedHttpClient, new ObjectMapper());
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
    void returnsEmptyResult_getVaccineValueSet_ifHttpClientThrowsInterruptedException() throws IOException, InterruptedException {
        doThrow(new InterruptedException("Mocked Interruption")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, VaccineValueSetDto> result = this.client.getVaccineValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyResult_getAuthHolderValueSet_ifHttpClientThrowsInterruptedException() throws IOException, InterruptedException {
        doThrow(new InterruptedException("Mocked Interruption")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, AuthHolderValueSetDto> result = this.client.getAuthHolderValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyResult_getProphylaxisValueSet_ifHttpClientThrowsInterruptedException() throws IOException, InterruptedException {
        doThrow(new InterruptedException("Mocked Interruption")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, ProphylaxisValueSetDto> result = this.client.getProphylaxisValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyResult_getVaccineValueSet_ifHttpClientThrowsIOException() throws IOException, InterruptedException {
        doThrow(new IOException("Mocked IOException")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, VaccineValueSetDto> result = this.client.getVaccineValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyResult_getAuthHolderValueSet_ifHttpClientThrowsIOException() throws IOException, InterruptedException {
        doThrow(new IOException("Mocked IOException")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, AuthHolderValueSetDto> result = this.client.getAuthHolderValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsEmptyResult_getProphylaxisValueSet_ifHttpClientThrowsIOException() throws IOException, InterruptedException {
        doThrow(new IOException("Mocked IOException")).when(mockedHttpClient).send(any(), any());
        VaccineImportControl importControl = VaccineImportControl.builder()
                .importDate(LocalDate.now())
                .importVersion("2.9.0")
                .done(false)
                .build();
        Map<String, ProphylaxisValueSetDto> result = this.client.getProphylaxisValueSet(importControl);
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}
