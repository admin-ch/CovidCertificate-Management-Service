package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import java.util.List;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_TEST;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryCertificateCreateDto extends CertificateCreateDto {

    private List<@Valid RecoveryCertificateDataDto> recoveryInfo;

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

    @AssertTrue(message = "Invalid country of test")
    public boolean isValidCountryOfTestOfRecoveryInfos() {
        return this.recoveryInfo == null || this.recoveryInfo.stream().allMatch(this::isValidCountryOfTestOfRecoveryInfo);
    }

    private boolean isValidCountryOfTestOfRecoveryInfo(RecoveryCertificateDataDto recoveryCertificateDataDto) {
        if (getSystemSource() == null) return true;
        final boolean isCountryCH = Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equalsIgnoreCase(recoveryCertificateDataDto.getCountryOfTest());
        switch (getSystemSource()) {
            case WebUI: {
                break;
            }
            case CsvUpload, ApiGateway: {
                // the source requires switzerland
                if (!isCountryCH) {
                    return false;
                }
                break;
            }
            case ApiPlatform: {
                // this source requires foreign countries
                if (isCountryCH) {
                    return false;
                }
                break;
            }
            default:
                return false;
        }
        return true;
    }

    @AssertTrue(message = "No recovery data specified")
    public boolean isRecoveryDataValid() {
        return !(recoveryInfo == null || recoveryInfo.isEmpty());
    }

    @AssertFalse(message = "Invalid dateOfBirth! Must be before the certificate date")
    public boolean isValidDateOfBirth() {
        return Stream.ofNullable(recoveryInfo).findFirst().orElse(List.of()).stream().anyMatch(dto -> isBirthdateAfter(dto.getDateOfFirstPositiveTestResult()));
    }
}
