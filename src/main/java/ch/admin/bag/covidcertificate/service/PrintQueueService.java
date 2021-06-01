package ch.admin.bag.covidcertificate.service;


import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PrintQueueService {
    public boolean sendPrintJob(CovidCertificateAddressDto addressDto) {
        log.info("Call to print certificate");
        return true;
    }
}
