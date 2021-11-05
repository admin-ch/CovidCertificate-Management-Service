package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(ProfileRegistry.SIGNING_SERVICE_MOCK)
public class MockSigningClient implements SigningClient {

    public byte[] createSignature(byte[] payload, SigningInformation signingInformation){
        log.info("Call the mock signing service");
        return payload;
    }

    public boolean verifySignature(VerifySignatureRequestDto verifySignatureRequestDto){
        return true;
    }

    public String getKeyIdentifier(String certificateAlias){
        return UUID.randomUUID().toString();
    }
}
