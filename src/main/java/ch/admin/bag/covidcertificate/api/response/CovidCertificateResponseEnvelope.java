package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.domain.enums.Delivery;
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
    private Delivery deliveryForKpi;
}
