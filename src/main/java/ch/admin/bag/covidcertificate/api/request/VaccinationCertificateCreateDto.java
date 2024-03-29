package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_VACCINATION_DATA;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccinationCertificateCreateDto extends CertificateCreateDto {

    private List<VaccinationCertificateDataDto> vaccinationInfo;

    public VaccinationCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<VaccinationCertificateDataDto> vaccinationInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.vaccinationInfo = vaccinationInfo;
    }

    @Override
    public void validate() {
        super.validate();
        if (vaccinationInfo == null || vaccinationInfo.isEmpty()) {
            throw new CreateCertificateException(NO_VACCINATION_DATA);
        } else {
            vaccinationInfo.forEach(VaccinationCertificateDataDto::validate);
        }

        if (vaccinationInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getVaccinationDate()))) {
            throw new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE);
        }
    }
}
