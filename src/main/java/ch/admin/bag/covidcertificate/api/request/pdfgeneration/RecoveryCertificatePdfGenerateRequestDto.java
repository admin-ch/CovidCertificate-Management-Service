package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryCertificatePdfGenerateRequestDto {
    @JsonProperty("issuedAt")
    private Long issuedAt;
    @JsonProperty("language")
    private String language;
    @JsonProperty("hcert")
    private String hcert;
    @JsonProperty("decodedCert")
    private RecoveryCertificateHcertDecodedDto decodedCert;
}
