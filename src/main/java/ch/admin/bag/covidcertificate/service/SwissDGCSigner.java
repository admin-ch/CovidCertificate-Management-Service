package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import lombok.Builder;
import org.springframework.stereotype.Service;
import se.digg.dgc.signatures.DGCSigner;

import java.time.Instant;

@Builder
@Service
public class SwissDGCSigner implements DGCSigner {

    private final COSEService coseService;

    public byte[] sign(byte[] dgcCBOR, SigningInformationDto signingInformation, Instant expiredAt) {
        return coseService.getCOSESign1(dgcCBOR, signingInformation, expiredAt);
    }

    // Parameter expiration is not used! We have to support it because of the interface signature.
    @Override
    public byte[] sign(byte[] dgcCBOR, Instant expiration) {
        return coseService.getCOSESign1(dgcCBOR, null, expiration);
    }

    @Override
    public Instant getSignerExpiration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSignerCountry() {
        throw new UnsupportedOperationException();
    }
}
