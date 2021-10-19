package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.client.signing.SigningClient;
import ch.admin.bag.covidcertificate.domain.SigningInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_PAYLOAD_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_PROTECTED_HEADER_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_SIGN1_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_COSE_SIGNATURE_DATA_FAILED;
import static ch.admin.bag.covidcertificate.api.Constants.CREATE_SIGNATURE_FAILED;

@Service
@Slf4j
@RequiredArgsConstructor
public class COSEService {
    private final CBORService cborService;
    private final SigningClient signingClient;

    public byte[] getCOSESign1(byte[] dgcCBOR, SigningInformation signingInformation) {
        byte[] protectedHeader = getProtectedHeader(signingInformation);
        byte[] payload = getPayload(dgcCBOR);
        byte[] signatureData = getSignatureData(protectedHeader, payload);
        byte[] signature = getSignature(signatureData, signingInformation);
        return getCOSESign1(protectedHeader, payload, signature);
    }

    private byte[] getProtectedHeader(SigningInformation signingInformation) {
        try {
            //TODO: keyIdentifier should be deleted. It is deprecated and used only for backwards compatibility.
            var keyIdentifier = signingInformation.getKeyIdentifier();
            if(signingInformation.getCertificateAlias() != null && !signingInformation.getCertificateAlias().isBlank()){
                keyIdentifier = signingClient.getKeyIdentifier(signingInformation.getCertificateAlias());
            }
            return cborService.getProtectedHeader(keyIdentifier);
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

    private byte[] getSignature(byte[] signatureData, SigningInformation signingInformation) {
        try {
            return signingClient.createSignature(signatureData, signingInformation);
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
