package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AntibodyCertificateHcertDecodedDto {

    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CertificatePersonDto personData;
    @JsonProperty("t")
    private List<AntibodyCertificateHcertDecodedDataDto> antibodyInfo;
}
