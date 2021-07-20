package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestCertificateHcertDecodedDataDto {
    @JsonProperty("tg")
    private String diseaseOrAgentTargeted;
    @JsonProperty("tt")
    private String typeOfTest;
    @JsonProperty("nm")
    private String testName;
    @JsonProperty("ma")
    private String testManufacturer;
    @JsonProperty("sc")
    private ZonedDateTime sampleDateTime;
    @JsonProperty("tr")
    private String result;
    @JsonProperty("tc")
    private String testingCentreOrFacility;
    @JsonProperty("co")
    private String memberStateOfTest;
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
