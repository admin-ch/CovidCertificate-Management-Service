package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryCertificatePdfGenerateMetadataDto extends CertificatePdfGenerateMetadataDto {
    @JsonProperty("df")
    private LocalDate validFrom;
    @JsonProperty("du")
    private LocalDate validUntil;

    public RecoveryCertificatePdfGenerateMetadataDto(String issuer, String identifier, LocalDate validFrom, LocalDate validUntil) {
        super(issuer, identifier);
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }
}
