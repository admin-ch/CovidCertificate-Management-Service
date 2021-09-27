package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(ProfileRegistry.SIGNING_SERVICE_MOCK)
public class MockSigningClient implements SigningClient {

    public byte[] create(byte[] payload, SigningInformation signingInformation){
        log.info("Call the mock signing service");
        return payload;
    }
}
