package ch.admin.bag.covidcertificate.client.printing.domain;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CertificatePrintRequestDto {
    @NotNull
    private byte[] pdfCertificate;

    @NotNull
    private String uvci;

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private int zipCode;
    private String city;
    private String language;
}
