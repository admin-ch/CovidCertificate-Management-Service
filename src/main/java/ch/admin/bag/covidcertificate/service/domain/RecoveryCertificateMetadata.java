package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryCertificateMetadata extends CovidCertificateMetadata {
    @JsonProperty("df")
    private LocalDate validFrom;
    @JsonProperty("du")
    private LocalDate validUntil;

    public RecoveryCertificateMetadata(String issuer, String identifier, LocalDate validFrom, LocalDate validUntil) {
        super(issuer, identifier);
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }
}
