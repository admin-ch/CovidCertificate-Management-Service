package ch.admin.bag.covidcertificate.api.request.conversion;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.ConvertCertificateException;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificateHcertDecodedDto;
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
public class VaccinationCertificateConversionRequestDto {

    @JsonProperty("conversionReason")
    private ConversionReason conversionReason;

    @JsonProperty("decodedCert")
    private VaccinationCertificateHcertDecodedDto decodedCert;

    public void validate() {
        switch (this.conversionReason) {
            case VACCINATION_CONVERSION:
                validateU18();
                break;
            default:
                throw new ConvertCertificateException(Constants.CREATE_PAYLOAD_FAILED);
        }
    }

    private void validateU18() {

    }
}
