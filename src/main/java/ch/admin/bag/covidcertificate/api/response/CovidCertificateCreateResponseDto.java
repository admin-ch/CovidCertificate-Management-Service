package ch.admin.bag.covidcertificate.api.response;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
}
