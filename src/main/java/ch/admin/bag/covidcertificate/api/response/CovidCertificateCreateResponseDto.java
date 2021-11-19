package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class CovidCertificateCreateResponseDto {
    @NonNull
    private byte[] pdf;
    @NonNull
    private byte[] qrCode;
    @NonNull
    private String uvci;
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CreateCertificateError appDeliveryError;


    public void validate() {
        if (ArrayUtils.isEmpty(pdf)) throw new CreateCertificateException(Constants.CREATE_PDF_FAILED);
        if (ArrayUtils.isEmpty(qrCode)) throw new CreateCertificateException(Constants.CREATE_BARCODE_FAILED);
        if (StringUtils.isEmpty(uvci)) throw new CreateCertificateException(Constants.CREATE_UVCI_FAILED);
    }
}
