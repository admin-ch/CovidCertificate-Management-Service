package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_LANGUAGE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_PERSON_DATA;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class CertificateCreateDto {
    @JsonUnwrapped
    private CovidCertificatePersonDto personData;
    private String language;
    private CovidCertificateAddressDto address;

    protected CertificateCreateDto(CovidCertificatePersonDto personData, String language) {
        this.personData = personData;
        this.language = language;
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
        if (address != null) {
            address.validate();
        }
    }
}
