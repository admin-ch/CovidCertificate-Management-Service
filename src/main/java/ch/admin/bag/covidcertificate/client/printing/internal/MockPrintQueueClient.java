package ch.admin.bag.covidcertificate.client.printing.internal;

import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile(ProfileRegistry.PRINTING_QUEUE_MOCK)
public class MockPrintQueueClient implements PrintQueueClient {
    @Override
    public boolean sendPrintJob(CovidCertificateAddressDto addressDto) {
        log.info("Call the mock print queue service");
        return true;
    }
}
