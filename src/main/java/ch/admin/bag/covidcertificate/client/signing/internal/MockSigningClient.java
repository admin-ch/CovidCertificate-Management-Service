package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import ch.admin.bag.covidcertificate.config.ProfileRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.HexEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(ProfileRegistry.SIGNING_SERVICE_MOCK)
public class MockSigningClient implements SigningClient {

    public byte[] createSignature(byte[] payload, SigningInformationDto signingInformation) {
        log.info("Call the mock signing service");
        return payload;
    }

    public boolean verifySignature(VerifySignatureRequestDto verifySignatureRequestDto){
        return true;
    }

    @SneakyThrows
    public String getKeyIdentifier(Integer slotNumber, String certificateAlias){
        var outputStream = new ByteArrayOutputStream();
        new HexEncoder().encode(UUID.randomUUID().toString().getBytes(), 0, 8, outputStream);
        return outputStream.toString();
    }

    @Override
    public void cleanKeyIdentifierCache() {
        log.info("Mocking Cache Cleanup");
    }
}
