package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.DEFAULT_DISEASE_OR_AGENT_SYSTEM;
import static ch.admin.bag.covidcertificate.api.Constants.DEFAULT_DISEASE_OR_AGENT_TARGETED;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePdfGenerateDiseaseOrAgentTargetedDto {

    @JsonProperty("code")
    private String code;
    @JsonProperty("system")
    private String system;

    public static CertificatePdfGenerateDiseaseOrAgentTargetedDto getStandardInstance() {
        return new CertificatePdfGenerateDiseaseOrAgentTargetedDto(
                DEFAULT_DISEASE_OR_AGENT_TARGETED,
                DEFAULT_DISEASE_OR_AGENT_SYSTEM
        );
    }
}
