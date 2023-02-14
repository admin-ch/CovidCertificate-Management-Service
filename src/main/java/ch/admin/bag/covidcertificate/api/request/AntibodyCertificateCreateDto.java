package ch.admin.bag.covidcertificate.api.request;

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
import java.util.stream.Stream;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AntibodyCertificateCreateDto extends CertificateCreateDto {

    @NotNull(message = "No antibody data specified")
    @Size(min = 1, message = "No antibody data specified")
    private List<@Valid AntibodyCertificateDataDto> antibodyInfo;

    public AntibodyCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<AntibodyCertificateDataDto> antibodyInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.antibodyInfo = antibodyInfo;
    }

    @AssertTrue()
    public boolean isValidSystemSource() {
        if (getSystemSource() == null) return true;
        switch (getSystemSource()) {
            case WebUI, CsvUpload, ApiGateway: {
                break;
            }
            case ApiPlatform: {
                throw new AccessDeniedException("Antibody certificates can't be generated through the ApiPlatform.");
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
        return true;
    }

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isValidDateOfBirth() {
        return Stream.ofNullable(antibodyInfo).findFirst().orElse(List.of()).stream().anyMatch(dto -> isBirthdateAfter(dto.getSampleDate()));
    }
}
