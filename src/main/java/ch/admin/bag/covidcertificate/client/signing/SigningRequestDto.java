package ch.admin.bag.covidcertificate.client.signing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class SigningRequestDto {
    private String dataToSign;
    private String signingKeyAlias;
    @JsonProperty("keyStoreSlot")
    private Integer slotNumber;
}