package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import static ch.admin.bag.covidcertificate.api.Constants.DEFAULT_DISEASE_OR_AGENT_SYSTEM;
import static ch.admin.bag.covidcertificate.api.Constants.DEFAULT_DISEASE_OR_AGENT_TARGETED;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateDiseaseOrAgentTargeted {
    @JsonProperty("code")
    private String code;
    @JsonProperty("system")
    private String system;

    public static CovidCertificateDiseaseOrAgentTargeted getStandardInstance() {
        return new CovidCertificateDiseaseOrAgentTargeted(
                DEFAULT_DISEASE_OR_AGENT_TARGETED,
                DEFAULT_DISEASE_OR_AGENT_SYSTEM
        );
    }
}
