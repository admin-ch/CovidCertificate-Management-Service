package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePdfGeneratePersonDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationTouristCertificateHcertDecodedDataDto;
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
public class UserDto {
    private List<String> roles;
}
