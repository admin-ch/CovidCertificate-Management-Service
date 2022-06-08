package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePersonDto {
    @JsonProperty("nam")
    private CertificatePersonNameDto name;
    @JsonProperty("dob")
    private String dateOfBirth;
}
