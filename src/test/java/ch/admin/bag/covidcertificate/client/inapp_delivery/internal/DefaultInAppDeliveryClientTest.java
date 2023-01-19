package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.security.authentication.JeapAuthenticationToken;
import ch.admin.bag.covidcertificate.config.security.authentication.ServletJeapAuthorization;
import ch.admin.bag.covidcertificate.service.KpiDataService;
import ch.admin.bag.covidcertificate.util.UserExtIdHelper;
import com.flextrade.jfixture.JFixture;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static ch.admin.bag.covidcertificate.FixtureCustomization.createUVCI;
import static ch.admin.bag.covidcertificate.api.Constants.APP_DELIVERY_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.UNKNOWN_APP_CODE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DefaultInAppDeliveryClientTest {

    static final JFixture fixture = new JFixture();
    static MockWebServer mockInAppDeliveryService;

    static ServletJeapAuthorization jeapAuthorization;

    static JeapAuthenticationToken jeapAuthenticationToken;

    static Jwt token;

    static KpiDataService kpiLogService;

    @InjectMocks
    private DefaultInAppDeliveryClient inAppDeliveryClient;

    private InAppDeliveryRequestDto requestDto;

    @BeforeAll
    static void setUp() throws IOException {
        jeapAuthorization = mock(ServletJeapAuthorization.class);
        jeapAuthenticationToken = mock(JeapAuthenticationToken.class);
        token = mock(Jwt.class);
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
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        when(jeapAuthenticationToken.getToken()).thenReturn(token);
        when(UserExtIdHelper.extractUserExtId(token, anyString(), SystemSource.WebUI)).thenReturn("test_ext_id");

        var deliveryStatus = assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(
                createUVCI(),
                SystemSource.WebUI,
                "0815",
                this.requestDto));
        assertNull(deliveryStatus);
    }

    @Test
    void throwsException__ifResponseCode418() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(418));

        var deliveryStatus = assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(
                createUVCI(),
                SystemSource.WebUI,
                "0815",
                this.requestDto));
        assertEquals(UNKNOWN_APP_CODE, deliveryStatus);
    }

    @Test
    void returnsTechnicalError__ifResponseCode500() {
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(500));

        var deliveryStatus = assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(
                createUVCI(),
                SystemSource.WebUI,
                "0815",
                this.requestDto));
        assertEquals(APP_DELIVERY_FAILED, deliveryStatus);
    }

    @Test
    void throwsException__ifServiceUnreachable() {
        ReflectionTestUtils.setField(this.inAppDeliveryClient, "serviceUri", "http://127.0.0.1");

        var deliveryStatus = assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(
                createUVCI(),
                SystemSource.WebUI,
                "0815",
                this.requestDto));
        assertEquals(APP_DELIVERY_FAILED, deliveryStatus);
    }

    @Test
    void logsKpi__ifDeliverySuccessful() {
        when(jeapAuthorization.getJeapAuthenticationToken()).thenReturn(jeapAuthenticationToken);
        when(jeapAuthenticationToken.getToken()).thenReturn(token);
        when(UserExtIdHelper.extractUserExtId(token, anyString(), SystemSource.WebUI)).thenReturn("test_ext_id");
        mockInAppDeliveryService.enqueue(new MockResponse().setResponseCode(200));

        assertDoesNotThrow(() -> this.inAppDeliveryClient.deliverToApp(
                createUVCI(),
                SystemSource.WebUI,
                "0815",
                this.requestDto));
        verify(kpiLogService, times(1)).saveKpiData(any());
    }

    @AfterAll
    static void tearDown() throws Throwable {
        mockInAppDeliveryService.shutdown();
    }
}