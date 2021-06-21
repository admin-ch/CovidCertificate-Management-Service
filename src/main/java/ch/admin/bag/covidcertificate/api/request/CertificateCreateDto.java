package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import org.springframework.util.StringUtils;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class CertificateCreateDto {
    @JsonUnwrapped
    private CovidCertificatePersonDto personData;
    private String language;
    private CovidCertificateAddressDto address;
    private String inAppDeliveryCode;

    public CertificateCreateDto(CovidCertificatePersonDto personData, String language, CovidCertificateAddressDto address, String inAppDeliveryCode) {
        this.personData = personData;
        this.language = language;
        this.address = address;
        this.inAppDeliveryCode = inAppDeliveryCode != null ? inAppDeliveryCode.toUpperCase() : null;
    }

    public boolean sendToPrint() {
        return this.address != null;
    }

    public boolean sendToApp() {
        return this.inAppDeliveryCode != null;
    }

    public void validate() {
        if (personData == null) {
            throw new CreateCertificateException(NO_PERSON_DATA);
        } else {
            personData.validate();
        }
        if (!AcceptedLanguages.isAcceptedLanguage(language)) {
            throw new CreateCertificateException(INVALID_LANGUAGE);
        }
        this.validateDeliveryMethod();
    }

    private void validateDeliveryMethod() {
        if (this.address != null && StringUtils.hasText(this.inAppDeliveryCode)) {
            throw new CreateCertificateException(DUPLICATE_DELIVERY_METHOD);
        } else {
            if (this.address != null) {
                this.address.validate();
            }
            if (StringUtils.hasText(this.inAppDeliveryCode)) {
                var isAlphaNumeric = org.apache.commons.lang3.StringUtils.isAlphanumeric(this.inAppDeliveryCode);
                var isNineCharsLong = this.inAppDeliveryCode.length() == 9;
                if (!isAlphaNumeric || !isNineCharsLong) {
                    throw new CreateCertificateException(INVALID_IN_APP_CODE);
                }
            }
        }
    }
}
