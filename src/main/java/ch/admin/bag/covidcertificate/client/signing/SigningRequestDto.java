package ch.admin.bag.covidcertificate.client.signing;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class SigningRequestDto {
    private String dataToSign;
    private String signingKeyAlias;
    private String certificateAlias;
}