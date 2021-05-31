package ch.admin.bag.covidcertificate.service;

import lombok.Builder;
import se.digg.dgc.signatures.DGCSigner;

import java.time.Instant;

@Builder
public class SwissDGCSigner implements DGCSigner {

    private final COSEService coseService;

    // Parameter expiration is not used! We have to support it because of the interface signature.
    @Override
    public byte[] sign(byte[] dgcCBOR, Instant expiration) {
        return coseService.getCOSESign1(dgcCBOR);
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
