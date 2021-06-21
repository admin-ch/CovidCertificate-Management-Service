package ch.admin.bag.covidcertificate.client.inapp_delivery;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.inapp_delivery.domain.InAppDeliveryRequestDto;

public interface InAppDeliveryClient {
    /**
     * Sends an InApp delivery request to the app backend. If the request fails a CreateCertificateException is thrown.
     *
     * @param requestDto - data to be sent to the app.
     */
    void deliverToApp(InAppDeliveryRequestDto requestDto) throws CreateCertificateException;
}
