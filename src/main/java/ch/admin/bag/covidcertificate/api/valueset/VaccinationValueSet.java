package ch.admin.bag.covidcertificate.api.valueset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class VaccinationValueSet {
    @JsonProperty("name")
    private String medicinalProduct;
    @JsonProperty("code")
    private String medicinalProductCode;
    @JsonProperty("prophylaxis")
    private String prophylaxis;
    @JsonProperty("prophylaxis_code")
    private String prophylaxisCode;
    @JsonProperty("auth_holder")
    private String authHolder;
    @JsonProperty("auth_holder_code")
    private String authHolderCode;
    private boolean active;
}
