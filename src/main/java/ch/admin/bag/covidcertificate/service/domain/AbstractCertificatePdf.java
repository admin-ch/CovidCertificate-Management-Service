package ch.admin.bag.covidcertificate.service.domain;


import ch.admin.bag.covidcertificate.api.request.CertificateType;
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
    protected CertificateType type;

    protected AbstractCertificatePdf(String familyName, String givenName, String dateOfBirth, String identifier, String language, CertificateType type) {
        this.familyName = familyName;
        this.givenName = givenName;
        this.dateOfBirth = dateOfBirth;
        this.identifier = identifier;
        this.language = language;
        this.type = type;
    }

    public boolean isEvidence() {
        return false;
    }

    public boolean showValidOnlyInSwitzerland() { return false; }
}
