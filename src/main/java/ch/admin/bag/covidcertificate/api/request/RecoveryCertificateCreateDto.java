package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_RECOVERY_DATA;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryCertificateCreateDto extends CertificateCreateDto {

    private List<RecoveryCertificateDataDto> recoveryInfo;

    public RecoveryCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<RecoveryCertificateDataDto> recoveryInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.recoveryInfo = recoveryInfo;
    }

    @Override
    public void validate() {
        super.validate();
        if (recoveryInfo == null || recoveryInfo.isEmpty()) {
            throw new CreateCertificateException(NO_RECOVERY_DATA);
        } else {
            recoveryInfo.forEach(
                    recoveryCertificateDataDto -> recoveryCertificateDataDto.validate(this.getSystemSource()));
        }

        if (recoveryInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getDateOfFirstPositiveTestResult()))) {
            throw new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE);
        }
    }
}
