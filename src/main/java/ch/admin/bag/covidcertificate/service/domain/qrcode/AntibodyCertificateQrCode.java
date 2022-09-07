package ch.admin.bag.covidcertificate.service.domain.qrcode;

import ch.admin.bag.covidcertificate.service.domain.AntibodyCertificateData;
import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import ch.admin.bag.covidcertificate.service.domain.qrcode.AbstractCertificateQrCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AntibodyCertificateQrCode implements AbstractCertificateQrCode {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CovidCertificatePerson personData;
    @JsonProperty("t")
    private List<AntibodyCertificateData> antibodyInfo;
}
