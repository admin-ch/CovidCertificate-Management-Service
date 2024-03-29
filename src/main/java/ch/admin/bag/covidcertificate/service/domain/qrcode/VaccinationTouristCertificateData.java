package ch.admin.bag.covidcertificate.service.domain.qrcode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationTouristCertificateData {
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
