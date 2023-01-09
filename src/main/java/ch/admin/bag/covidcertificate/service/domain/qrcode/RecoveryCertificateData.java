package ch.admin.bag.covidcertificate.service.domain.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryCertificateData {
    @JsonProperty("tg")
    private String diseaseOrAgentTargeted;
    @JsonProperty("fr")
    private LocalDate dateOfFirstPositiveTestResult;
    @JsonProperty("co")
    private String countryOfTest;
    @JsonProperty("df")
    private LocalDate validFrom;
    @JsonProperty("du")
    private LocalDate validUntil;
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
