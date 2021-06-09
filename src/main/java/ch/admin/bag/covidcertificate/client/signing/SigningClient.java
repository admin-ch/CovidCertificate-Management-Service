package ch.admin.bag.covidcertificate.client.signing;

public interface SigningClient {

     byte[] create(byte[] payload);
}
