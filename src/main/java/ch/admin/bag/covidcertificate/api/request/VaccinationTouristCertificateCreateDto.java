package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_VACCINATION_DATA;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccinationTouristCertificateCreateDto extends CertificateCreateDto {

    @JsonProperty("vaccinationTouristInfo")
    private List<@Valid VaccinationCertificateDataDto> vaccinationTouristInfo;

    public VaccinationTouristCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<VaccinationCertificateDataDto> vaccinationTouristInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.vaccinationTouristInfo = vaccinationTouristInfo;
    }

//    @Override
    public void validate() {
//        super.validate();
        if (vaccinationTouristInfo == null || vaccinationTouristInfo.isEmpty()) {
            throw new CreateCertificateException(NO_VACCINATION_DATA);
        } else {
//            vaccinationTouristInfo.forEach(VaccinationTouristCertificateDataDto::validate);
        }

        if (vaccinationTouristInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getVaccinationDate()))) {
            throw new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE);
        }
    }

    @Override
    public boolean isDeliverablePerPost() {
        return false;
    }
}
