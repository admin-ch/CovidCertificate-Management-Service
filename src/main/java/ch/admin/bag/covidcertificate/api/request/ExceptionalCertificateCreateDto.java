package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.AccessDeniedException;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_EXCEPTIONAL_INFO;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionalCertificateCreateDto extends CertificateCreateDto {

    @NotNull(message = "No exceptional data specified")
    @Size(min = 1, message = "No exceptional data specified")
    private List<@Valid ExceptionalCertificateDataDto> exceptionalInfo;

    public ExceptionalCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<ExceptionalCertificateDataDto> exceptionalInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.exceptionalInfo = exceptionalInfo;
    }

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isBirthdateAfterValidation() {
        if (Objects.isNull(getPersonData().getDateOfBirth()) || Objects.isNull(exceptionalInfo)) return false;
        try {
            return exceptionalInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getValidFrom()));
        } catch (CreateCertificateException e) {
            return true;
        }
    }


    @AssertTrue()
    public boolean isValidSystemSource() {
        if (getSystemSource() == null) return true;
        switch (getSystemSource()) {
            case WebUI, CsvUpload: {
                break;
            }
            case ApiGateway, ApiPlatform: {
                throw new AccessDeniedException("Exceptional certificates can't be generated through the ApiPlatform or the ApitGateway.");
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
        return true;
    }

}
