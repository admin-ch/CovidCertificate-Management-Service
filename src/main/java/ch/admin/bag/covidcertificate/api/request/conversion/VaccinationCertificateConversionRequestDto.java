package ch.admin.bag.covidcertificate.api.request.conversion;

import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificateHcertDecodedDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VaccinationCertificateConversionRequestDto {

    @JsonProperty("conversionReason")
    @CertificateConversionConstraint
    private ConversionReason conversionReason;

    @JsonProperty("decodedCert")
    private VaccinationCertificateHcertDecodedDto decodedCert;

}
