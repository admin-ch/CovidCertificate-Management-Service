package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;
import org.springframework.util.StringUtils;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonNameDto {

    private String familyName;

    private String givenName;

    public void validate() {
        if (!StringUtils.hasText(givenName) || givenName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }
        if (!StringUtils.hasText(familyName) || familyName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }
    }
}
