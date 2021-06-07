package ch.admin.bag.covidcertificate.api.response;

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
    @Setter(AccessLevel.PUBLIC)
    private boolean printJobSent;
}
