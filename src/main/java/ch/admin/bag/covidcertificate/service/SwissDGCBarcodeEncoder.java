package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.upokecenter.cbor.CBORException;
import lombok.extern.slf4j.Slf4j;
import se.digg.dgc.encoding.*;
import se.digg.dgc.service.impl.DefaultDGCBarcodeEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.time.Instant;

@Slf4j
public class SwissDGCBarcodeEncoder extends DefaultDGCBarcodeEncoder {
    private SwissDGCSigner dgcSigner;
    private BarcodeCreator barcodeCreator;

    public SwissDGCBarcodeEncoder(SwissDGCSigner dgcSigner, BarcodeCreator barcodeCreator) {
        super(dgcSigner, barcodeCreator);
        this.dgcSigner = dgcSigner;
        this.barcodeCreator = barcodeCreator;
    }

    public String encode(final byte[] dcc, final Instant expiration, final SigningInformation signingInformation) throws IOException, SignatureException {

        log.trace("Encoding to Base45 from CBOR-encoded DCC-payload (length: {}) ...", dcc.length);

        // Create a signed CWT ...
        //
        byte[] cwt = this.sign(dcc, expiration, signingInformation);

        // Compression and Base45 encoding ...
        //
        log.trace("Compressing the signed CWT of length {} ...", cwt.length);
        cwt = Zlib.compress(cwt);
        log.trace("Signed CWT was compressed into {} bytes", cwt.length);

        log.trace("Base45 encoding compressed CWT ...");
        final String base45 = Base45.getEncoder().encodeToString(cwt);
        log.trace("Base45 encoding: {}", base45);

        return DGCConstants.DGC_V1_HEADER + base45;
    }

    public byte[] sign(final byte[] dcc, final Instant expiration, final SigningInformation signingInformation) throws IOException {

        try {
            // Sign the DGC ...
            //
            log.trace("Creating CWT and signing CBOR-encoded DCC (length: {}) ...", dcc.length);
            return this.dgcSigner.sign(dcc, expiration, signingInformation);
        }
        catch (final CBORException e) {
            log.info("Internal CBOR error - {}", e.getMessage(), e);
            throw new IOException("Internal CBOR error - " + e.getMessage(), e);
        }
    }

    public Barcode encodeToBarcode(final byte[] dcc, final Instant expiration, final SigningInformation signingInformation) throws IOException, SignatureException, BarcodeException {

        final String base45 = this.encode(dcc, expiration, signingInformation);

        // Create the Barcode ...
        //
        log.trace("Creating barcode ...");
        final Barcode barcode = this.barcodeCreator.create(base45, StandardCharsets.US_ASCII);
        log.trace("Successfully created: {}", barcode);

        return barcode;
    }
}
