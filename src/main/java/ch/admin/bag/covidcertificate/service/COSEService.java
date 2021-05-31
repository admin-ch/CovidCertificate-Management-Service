package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.SigningClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class COSEService {
    private final CBORService cborService;
    private final SigningClient signingClient;

    public byte[] getCOSESign1(byte[] dgcCBOR) {
        byte[] protectedHeader = getProtectedHeader();
        byte[] payload = getPayload(dgcCBOR);
        byte[] signatureData = getSignatureData(protectedHeader, payload);
        byte[] signature = getSignature(signatureData);
        return getCOSESign1(protectedHeader, payload, signature);
    }

    private byte[] getProtectedHeader() {
        try {
            return cborService.getProtectedHeader();
        } catch (Exception e) {
            throw new CreateCertificateException(CREATE_COSE_PROTECTED_HEADER_FAILED);
        }
    }

    private byte[] getPayload(byte[] hcert) {
        try {
            return cborService.getPayload(hcert);
        } catch (Exception e) {
            throw new CreateCertificateException(CREATE_COSE_PAYLOAD_FAILED);
        }
    }

    private byte[] getSignatureData(byte[] protectedHeader, byte[] payload) {
        try {
            return cborService.getSignatureData(protectedHeader, payload);
        } catch (Exception e) {
            throw new CreateCertificateException(CREATE_COSE_SIGNATURE_DATA_FAILED);
        }
    }

    private byte[] getSignature(byte[] signatureData) {
        try {
            return signingClient.create(signatureData);
        } catch (Exception e) {
            throw new CreateCertificateException(CREATE_SIGNATURE_FAILED);
        }
    }

    private byte[] getCOSESign1(byte[] protectedHeader, byte[] payload, byte[] signature) {
        try {
            return cborService.getCOSESign1(protectedHeader, payload, signature);
        } catch (Exception e) {
            throw new CreateCertificateException(CREATE_COSE_SIGN1_FAILED);
        }
    }
}
