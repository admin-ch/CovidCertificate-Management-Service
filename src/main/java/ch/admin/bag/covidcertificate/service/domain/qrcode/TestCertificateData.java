package ch.admin.bag.covidcertificate.service.domain.qrcode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestCertificateData {
    @JsonProperty("tg")
    private String diseaseOrAgentTargeted;
    @JsonProperty("tt")
    private String typeOfTest;
    @JsonProperty("ma")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
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
