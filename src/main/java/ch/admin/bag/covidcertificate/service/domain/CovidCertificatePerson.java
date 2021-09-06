package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePerson {
    @JsonProperty("nam")
    private CovidCertificatePersonName name;
    @JsonProperty("dob")
    private String dateOfBirth;
}
