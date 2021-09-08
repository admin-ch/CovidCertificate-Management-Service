package ch.admin.bag.covidcertificate.service.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractCertificatePdf {
    protected String familyName;
    protected String givenName;
    protected String dateOfBirth;
    protected String identifier;
    protected String language;

    protected AbstractCertificatePdf(String familyName, String givenName, String dateOfBirth, String identifier, String language) {
        this.familyName = familyName;
        this.givenName = givenName;
        this.dateOfBirth = dateOfBirth;
        this.identifier = identifier;
        this.language = language;
    }
}
