package ch.admin.bag.covidcertificate.client.signing;

import ch.admin.bag.covidcertificate.domain.SigningInformation;

public interface SigningClient {

     byte[] create(byte[] payload, SigningInformation signingInformation);
}
