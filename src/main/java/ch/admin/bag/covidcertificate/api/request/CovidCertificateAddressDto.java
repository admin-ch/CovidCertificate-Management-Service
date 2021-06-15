package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedCantons;
import lombok.*;
import org.springframework.util.StringUtils;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateAddressDto {
    private static final int MAX_FIELD_LENGTH = 128;

    private String streetAndNr;
    private int zipCode;
    private String city;
    private String cantonCodeSender;

    public void validate() {
        if (this.hasInvalidLength(streetAndNr) || this.hasInvalidLength(city)) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
        if (zipCode < 1000 || zipCode > 9999) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
        if (!AcceptedCantons.isAccepted(this.cantonCodeSender)) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
    }

    /**
     * Validates that a given text has characters and is not larger than MAX_FIELD_LENGTH.
     *
     * @param text String to check.
     * @return boolean if the text contains characters and is not longer than  MAX_FIELD_LENGTH
     */
    private boolean hasInvalidLength(String text) {
        return !StringUtils.hasText(text) || text.length() > MAX_FIELD_LENGTH;
    }
}
