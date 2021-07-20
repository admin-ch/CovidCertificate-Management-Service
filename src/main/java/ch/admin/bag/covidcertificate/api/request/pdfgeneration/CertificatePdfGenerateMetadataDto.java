package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePdfGenerateMetadataDto {
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
