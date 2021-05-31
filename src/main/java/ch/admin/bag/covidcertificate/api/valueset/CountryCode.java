package ch.admin.bag.covidcertificate.api.valueset;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CountryCode {
    @JsonProperty("short")
    private String shortName;
    private String display;
    private String lang;
    private boolean active;
    private String version;
    private String system;
}
