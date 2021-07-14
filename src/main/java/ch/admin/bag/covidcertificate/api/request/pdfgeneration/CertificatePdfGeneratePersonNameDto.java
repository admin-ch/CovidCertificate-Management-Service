package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePdfGeneratePersonNameDto {
    @JsonProperty("fn")
    private String familyName;
    @JsonProperty("fnt")
    private String familyNameStandardised;
    @JsonProperty("gn")
    private String givenName;
    @JsonProperty("gnt")
    private String givenNameStandardised;
}
