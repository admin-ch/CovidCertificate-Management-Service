package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryRatCertificatePdfGenerateRequestDto {

    @JsonProperty("issuedAt")
    private Long issuedAt;
    @JsonProperty("language")
    private String language;
    @JsonProperty("hcert")
    private String hcert;
    @JsonProperty("decodedCert")
    private RecoveryRatCertificateHcertDecodedDto decodedCert;
}
