package ch.admin.bag.covidcertificate.api.request.pdfgeneration;

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
public class ExceptionalCertificateHcertDecodedDto {
    @JsonProperty("ver")
    private String version;
    @JsonUnwrapped
    private CertificatePdfGeneratePersonDto personData;
    @JsonProperty("t")
    private List<ExceptionalCertificateHcertDecodedDataDto> exceptionalInfo;
}
