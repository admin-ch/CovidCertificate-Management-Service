package ch.admin.bag.covidcertificate.client.printing.internal;

import ch.admin.bag.covidcertificate.client.printing.PrintQueueClient;
import ch.admin.bag.covidcertificate.client.printing.domain.CertificatePrintRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("!" + ProfileRegistry.PRINTING_QUEUE_MOCK)
public class DefaultPrintQueueClient implements PrintQueueClient {
    @Override
    public boolean sendPrintJob(CertificatePrintRequestDto printRequestDto) {
        throw new NotImplementedException("printing service connection not implemented");
    }
}
