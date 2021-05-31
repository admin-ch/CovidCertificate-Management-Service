package ch.admin.bag.covidcertificate.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryCertificateQrCode implements AbstractCertificateQrCode {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CovidCertificatePerson personData;
    @JsonProperty("r")
    private List<RecoveryCertificateData> recoveryInfo;
}
