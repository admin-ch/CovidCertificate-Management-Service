package ch.admin.bag.covidcertificate.client.printing;


import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;


public interface PrintQueueClient {
    boolean sendPrintJob(CertificatePrintRequestDto printRequestDto);
}
