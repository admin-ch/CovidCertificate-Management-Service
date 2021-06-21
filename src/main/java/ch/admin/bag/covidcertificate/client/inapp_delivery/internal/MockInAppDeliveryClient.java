package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(ProfileRegistry.INAPP_DELIVERY_SERVICE_MOCK)
public class MockInAppDeliveryClient implements InAppDeliveryClient {
    @Override
    public void deliverToApp(InAppDeliveryRequestDto requestDto) throws CreateCertificateException {
        log.info("Call the mock InApp delivery service");
    }
}
