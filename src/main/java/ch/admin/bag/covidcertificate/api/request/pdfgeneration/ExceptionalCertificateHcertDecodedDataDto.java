package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExceptionalCertificateHcertDecodedDataDto {

    @JsonProperty("tg")
    private String diseaseOrAgentTargeted;
    @JsonProperty("tt")
    private String typeOfTest;
    @JsonProperty("nm")
    private String testName;
    @JsonProperty("sc")
    private ZonedDateTime validFrom;
    @JsonProperty("tr")
    private String result;
    @JsonProperty("tc")
    private String attestationIssuer;
    @JsonProperty("co")
    private String country;
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
