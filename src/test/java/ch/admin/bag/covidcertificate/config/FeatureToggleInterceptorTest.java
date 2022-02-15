package ch.admin.bag.covidcertificate.config;

import ch.admin.bag.covidcertificate.api.exception.FeatureToggleException;
import ch.admin.bag.covidcertificate.api.request.CertificateType;
import ch.admin.bag.covidcertificate.config.featureToggle.FeatureData;
import ch.admin.bag.covidcertificate.config.featureToggle.FeatureToggleInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.List;

public class FeatureToggleInterceptorTest extends Assert {
    private final FeatureToggleInterceptor interceptor = new FeatureToggleInterceptor();
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;


    @Before
    public void setup() {
        FeatureData active = new FeatureData();
        active.setStart(LocalDateTime.now());
        active.setEnd(LocalDateTime.MAX);
        active.setType(CertificateType.TEST);
        active.setUris(List.of("active"));

        FeatureData inactive = new FeatureData();
        inactive.setStart(LocalDateTime.now().plusSeconds(1));
        inactive.setEnd(LocalDateTime.MAX);
        inactive.setType(CertificateType.ANTIBODY);
        inactive.setUris(List.of("inactive"));
        interceptor.setFeatures(List.of(active, inactive));

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        response = new MockHttpServletResponse();
    }

    @Test
    public void testActive() {
        request.setRequestURI("/api/v1/active");
        boolean wentThrough = interceptor.preHandle(request, response, new Object());
        assertTrue(wentThrough);
    }


    @Test
    public void testInactive() {
        request.setRequestURI("/api/v1/inactive");
        assertThrows(FeatureToggleException.class, () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void testNoConfig() {
        request.setRequestURI("/api/v1/noConfig");
        boolean wentThrough = interceptor.preHandle(request, response, new Object());
        assertTrue(wentThrough);
    }

    @Test
    public void testInactiveCSV() {
        request.setRequestURI("/api/v1/covidcertificate/csv");
        request.setParameter("certificateType", CertificateType.ANTIBODY.name());
        assertThrows(FeatureToggleException.class, () -> interceptor.preHandle(request, response, new Object()));
    }

    @Test
    public void testActiveCSV() {
        request.setRequestURI("/api/v1/covidcertificate/csv");
        request.setParameter("certificateType", CertificateType.TEST.name());
        boolean wentThrough = interceptor.preHandle(request, response, new Object());
        assertTrue(wentThrough);
    }

}
