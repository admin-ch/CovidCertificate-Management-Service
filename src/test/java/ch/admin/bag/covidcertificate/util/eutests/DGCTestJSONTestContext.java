package ch.admin.bag.covidcertificate.util.eutests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class DGCTestJSONTestContext {
    @JsonProperty("VERSION")
    private Integer version;
    @JsonProperty("SCHEMA")
    private String jsonSchema;
    @JsonProperty("CERTIFICATE")
    private String certificate;
    @JsonProperty("VALIDATIONCLOCK")
    private String validationClock;
    @JsonProperty("DESCRIPTION")
    private String description;
}
