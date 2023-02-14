package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class VaccinationTouristCertificateCreateDto extends CertificateCreateDto {

    @NotNull(message = "No vaccination data was specified")
    @Size(min = 1, message = "No vaccination data was specified")
    private List<@Valid VaccinationCertificateDataDto> vaccinationTouristInfo;

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isBirthdateAfterValidation() {
        if (Objects.isNull(getPersonData().getDateOfBirth()) || Objects.isNull(vaccinationTouristInfo)) return false;
        try {
            return vaccinationTouristInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getVaccinationDate()));
        } catch (CreateCertificateException e) {
            return true;
        }
    }

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

    @Override
    public boolean isDeliverablePerPost() {
        return false;
    }
}
