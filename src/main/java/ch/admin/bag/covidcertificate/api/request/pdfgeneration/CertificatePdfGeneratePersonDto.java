package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePdfGeneratePersonDto {
    @JsonProperty("nam")
    private CertificatePdfGeneratePersonNameDto name;
    @JsonProperty("dob")
    private String dateOfBirth;
}
