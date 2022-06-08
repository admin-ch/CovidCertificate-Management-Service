package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.ConvertCertificateException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConvertedCertificateResponseDto {

    @NonNull
    private String payload;

    @NonNull
    private String uvci;

    public void validate() {
        if (StringUtils.isEmpty(payload)) {
            throw new ConvertCertificateException(Constants.CREATE_PAYLOAD_FAILED);
        }
        if (StringUtils.isEmpty(uvci)) {
            throw new ConvertCertificateException(Constants.CREATE_CONVERTED_UVCI_FAILED);
        }
    }
}
