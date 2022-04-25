package ch.admin.bag.covidcertificate.api.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CovidCertificateResponseEnvelope {
    private CovidCertificateCreateResponseDto responseDto;
    private String usedKeyIdentifier;
}
