package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonName {
    @JsonProperty("fn")
    private String familyName;
    @JsonProperty("fnt")
    private String familyNameStandardised;
    @JsonProperty("gn")
    private String givenName;
    @JsonProperty("gnt")
    private String givenNameStandardised;
}
