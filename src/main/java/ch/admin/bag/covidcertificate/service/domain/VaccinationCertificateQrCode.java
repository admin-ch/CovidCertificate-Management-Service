package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateQrCode implements AbstractCertificateQrCode {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CovidCertificatePerson personData;
    @JsonProperty("v")
    private List<VaccinationCertificateData> vaccinationInfo;
}
