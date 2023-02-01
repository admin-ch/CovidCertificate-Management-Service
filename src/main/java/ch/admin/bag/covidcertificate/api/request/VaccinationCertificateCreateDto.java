package ch.admin.bag.covidcertificate.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccinationCertificateCreateDto extends CertificateCreateDto {

    public VaccinationCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<VaccinationCertificateDataDto> certificateData,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource, certificateData);
    }

    @Override
    @JsonProperty("vaccinationInfo")
    public List<VaccinationCertificateDataDto> getCertificateData() {
        return super.getCertificateData();
    }
}
