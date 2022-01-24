package ch.admin.bag.covidcertificate.service.domain;

import ch.admin.bag.covidcertificate.api.request.CertificateType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ExceptionalCertificatePdf extends AbstractCertificatePdf {
    private final String diseaseOrAgentTargetedCode;
    private final String diseaseOrAgentTargetedSystem;
    private final LocalDate validFrom;
    private final String attestationIssuer;
    private final String country;
    private final String countryEn;

    private final String issuer;

    public ExceptionalCertificatePdf(
            String familyName,
            String givenName,
            String dateOfBirth,
            String language,
            String diseaseOrAgentTargetedCode,
            String diseaseOrAgentTargetedSystem,
            LocalDate validFrom,
            String attestationIssuer,
            String country,
            String countryEn,
            String issuer,
            String identifier
    ) {
        super(familyName, givenName, dateOfBirth, identifier, language,  CertificateType.EXCEPTIONAL);
        this.diseaseOrAgentTargetedCode = diseaseOrAgentTargetedCode;
        this.diseaseOrAgentTargetedSystem = diseaseOrAgentTargetedSystem;
        this.validFrom = validFrom;
        this.attestationIssuer = attestationIssuer;
        this.country = country;
        this.countryEn = countryEn;
        this.issuer = issuer;
    }

    @Override
    public boolean showValidOnlyInSwitzerland() { return true; }
}
