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
public class CertificatePersonNameDto {

    @JsonProperty("fn")
    private String familyName;
    @JsonProperty("fnt")
    private String familyNameStandardised;
    @JsonProperty("gn")
    private String givenName;
    @JsonProperty("gnt")
    private String givenNameStandardised;
}
