package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AntibodyCertificateHcertDecodedDto {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CertificatePdfGeneratePersonDto personData;
    @JsonProperty("t")
    private List<AntibodyCertificateHcertDecodedDataDto> antibodyInfo;
}
