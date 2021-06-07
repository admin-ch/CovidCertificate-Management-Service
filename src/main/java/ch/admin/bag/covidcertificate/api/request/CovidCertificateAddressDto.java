package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateAddressDto {
    private String line1;
    private String line2;
    private int zipCode;
    private String city;

    public void validate() {
        if (line1 == null || city == null) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
        if (zipCode < 1000 || zipCode > 9999) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
    }
}
