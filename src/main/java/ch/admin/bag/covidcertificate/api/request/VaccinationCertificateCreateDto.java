package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VaccinationCertificateCreateDto extends CertificateCreateDto {


    @NotNull(message = "No vaccination data was specified")
    @Size(min = 1, message = "No vaccination data was specified")
    private List<@Valid VaccinationCertificateDataDto> vaccinationInfo;

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isBirthdateAfterValidation() {
        if (Objects.isNull(getPersonData().getDateOfBirth()) || Objects.isNull(vaccinationInfo)) return false;
        try {
            return vaccinationInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getVaccinationDate()));
        } catch (CreateCertificateException e) {
            return true;
        }
    }

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
}
