package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateData {
    @JsonProperty("tg")
    private String diseaseOrAgentTargeted;
    @JsonProperty("vp")
    private String vaccineProphylaxis;
    @JsonProperty("mp")
    private String medicinalProduct;
    @JsonProperty("ma")
    private String marketingAuthorizationHolder;
    @JsonProperty("dn")
    private Integer numberOfDoses;
    @JsonProperty("sd")
    private Integer totalNumberOfDoses;
    @JsonProperty("dt")
    private LocalDate vaccinationDate;
    @JsonProperty("co")
    private String countryOfVaccination;
    @JsonProperty("is")
    private String issuer;
    @JsonProperty("ci")
    private String identifier;
}
