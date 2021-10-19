package ch.admin.bag.covidcertificate.client.signing;

import ch.admin.bag.covidcertificate.domain.SigningInformation;

public interface SigningClient {

     byte[] createSignature(byte[] payload, SigningInformation signingInformation);
     String getKeyIdentifier(String certificateAlias);

}
