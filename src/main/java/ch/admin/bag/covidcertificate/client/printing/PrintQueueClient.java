package ch.admin.bag.covidcertificate.client.printing;


import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;


public interface PrintQueueClient {
    boolean sendPrintJob(CovidCertificateAddressDto addressDto);
}
