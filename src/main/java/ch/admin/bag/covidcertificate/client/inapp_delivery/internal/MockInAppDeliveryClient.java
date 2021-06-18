package ch.admin.bag.covidcertificate.client.inapp_delivery.internal;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.InAppDeliveryClient;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(ProfileRegistry.INAPP_DELIVERY_SERVICE_MOCK)
public class MockInAppDeliveryClient implements InAppDeliveryClient {
    @Override
    public void deliverToApp(InAppDeliveryRequestDto requestDto) throws CreateCertificateException {
        // do nothing
    }
}
