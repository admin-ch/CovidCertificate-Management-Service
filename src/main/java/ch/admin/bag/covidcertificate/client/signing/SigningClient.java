package ch.admin.bag.covidcertificate.client.signing;

public interface SigningClient {

    byte[] createSignature(byte[] payload, SigningInformationDto signingInformation);

    boolean verifySignature(VerifySignatureRequestDto verifySignatureRequestDto);

    String getKeyIdentifier(String certificateAlias);

    void cleanKeyIdentifierCache();

}
