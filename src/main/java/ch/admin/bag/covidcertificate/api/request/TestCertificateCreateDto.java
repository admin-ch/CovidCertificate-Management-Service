package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
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
public class TestCertificateCreateDto extends CertificateCreateDto {

    @NotNull(message = "No test data was specified")
    @Size(min = 1, message = "No test data was specified")
    private List<@Valid TestCertificateDataDto> testInfo;

    public TestCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<TestCertificateDataDto> testInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource

    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.testInfo = testInfo;
    }

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isBirthdateAfterValidation() {
        if (Objects.isNull(getPersonData().getDateOfBirth()) || Objects.isNull(testInfo)) return false;
        try {
            return testInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getSampleDateTime()));
        } catch (CreateCertificateException e) {
            return true;
        }
    }

    @Override
    public boolean isDeliverablePerPost() {
        return false;
    }
}
