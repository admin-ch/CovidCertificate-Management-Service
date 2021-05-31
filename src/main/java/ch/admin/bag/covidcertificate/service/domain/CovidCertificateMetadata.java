package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateMetadata {
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
