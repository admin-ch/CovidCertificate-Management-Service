package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;

@Getter
@ToString
@AllArgsConstructor
public class CovidCertificateAddressDto {
    private String line1;
    private String line2;
    private String npa;
    private String city;

    public void validate() {
        if (line1 == null || npa == null || city == null) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
        if (npa.length() != 4 || !npa.matches("[0-9]+")) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
    }
}
