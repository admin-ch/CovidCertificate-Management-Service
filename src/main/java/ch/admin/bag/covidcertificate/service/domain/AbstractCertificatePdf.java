package ch.admin.bag.covidcertificate.service.domain;


import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public abstract class AbstractCertificatePdf {
    protected String familyName;
    protected String givenName;
    protected LocalDate dateOfBirth;
    protected String identifier;
    protected String language;

    protected AbstractCertificatePdf(String familyName, String givenName, LocalDate dateOfBirth, String identifier, String language) {
        this.familyName = familyName;
        this.givenName = givenName;
        this.dateOfBirth = dateOfBirth;
        this.identifier = identifier;
        this.language = language;
    }
}
