package ch.admin.bag.covidcertificate.api.valueset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TestValueSet {
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("type_code")
    private String typeCode;
    @JsonProperty("manufacturer")
    private String manufacturer;
    @JsonProperty("swiss_test_kit")
    private String swissTestKit;
    @JsonProperty("manufacturer_code_eu")
    private String manufacturerCodeEu;
    @JsonProperty("eu_accepted")
    private boolean euAccepted;
    @JsonProperty("ch_accepted")
    private boolean chAccepted;
    private boolean active;
}
