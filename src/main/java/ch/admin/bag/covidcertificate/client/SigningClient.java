package ch.admin.bag.covidcertificate.client;

public interface SigningClient {

     byte[] create(byte[] payload);
}
