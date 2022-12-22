package ch.admin.bag.covidcertificate.service.domain.qrcode;

import ch.admin.bag.covidcertificate.service.domain.CovidCertificatePerson;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestCertificateQrCode implements AbstractCertificateQrCode {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CovidCertificatePerson personData;
    @JsonProperty("t")
    private List<TestCertificateData> testInfo;
}
