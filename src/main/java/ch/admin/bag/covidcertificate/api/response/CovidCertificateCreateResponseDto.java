package ch.admin.bag.covidcertificate.api.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateCreateResponseDto {
    private byte[] pdf;
    private byte[] qrCode;
    private String uvci;
}
