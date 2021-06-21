package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import com.flextrade.jfixture.JFixture;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static ch.admin.bag.covidcertificate.api.Constants.INAPP_DELIVERY_FAILED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultInAppDeliveryClientTest {

    static final JFixture fixture = new JFixture();
    static MockWebServer mockInAppDeliveryService;

    static ServletJeapAuthorization jeapAuthorization;
    static KpiDataService kpiLogService;

    @InjectMocks
    private DefaultInAppDeliveryClient inAppDeliveryClient;

    private InAppDeliveryRequestDto requestDto;

    @BeforeAll
    static void setUp() throws IOException {
        jeapAuthorization = mock(ServletJeapAuthorization.class);
        kpiLogService = mock(KpiDataService.class);
        mockInAppDeliveryService = new MockWebServer();
        mockInAppDeliveryService.start();
    }

    @BeforeEach
    void initialize() {
        reset(jeapAuthorization, kpiLogService);
        this.requestDto = fixture.create(InAppDeliveryRequestDto.class);
        this.inAppDeliveryClient = new DefaultInAppDeliveryClient(WebClient.create(), jeapAuthorization, kpiLogService);
        ReflectionTestUtils.setField(this.inAppDeliveryClient, "serviceUri",
                String.format("http://localhost:%s/", mockInAppDeliveryService.getPort()));
    }

    @Test
    void doesSendInAppDeliverySuccessfully() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(200));

        assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
    }

    @Test
    void throwsException__ifResponseCode404() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(404));

        var exception = assertThrows(CreateCertificateException.class, () -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
        assertEquals(INAPP_DELIVERY_FAILED, exception.getError());
    }

    @Test
    void throwsException__ifResponseCode500() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(500));

        var exception = assertThrows(CreateCertificateException.class, () -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
        assertEquals(INAPP_DELIVERY_FAILED, exception.getError());
    }

    @Test
    void throwsException__ifServiceUnreachable() {
        ReflectionTestUtils.setField(this.inAppDeliveryClient, "serviceUri", "http://127.0.0.1");

        var exception = assertThrows(CreateCertificateException.class, () -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
        assertEquals(INAPP_DELIVERY_FAILED, exception.getError());
    }

    @Test
    void throwsException__ifResponseIsNull() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(0));

        var exception = assertThrows(CreateCertificateException.class, () -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
        assertEquals(INAPP_DELIVERY_FAILED, exception.getError());
    }

    @Test
    void logsKpi__ifDeliverySuccessful() {
        when(jeapAuthorization.getExtIdInAuthentication()).thenReturn("test_ext_id");
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(200));

        assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(this.requestDto));
        verify(this.kpiLogService, times(1)).log(any());
    }

    @AfterAll
    static void tearDown() throws Throwable {
        mockInAppDeliveryService.shutdown();
    }
}