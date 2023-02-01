package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.valueset.AllowedSenders;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.Range;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificateAddressDto {
    private static final int MAX_FIELD_LENGTH = 128;
    private static final Range<Integer> ZIPCODE_VALUE_RANGE=Range.between(1000, 9999);

    @Size(max = MAX_FIELD_LENGTH, message = "Paper-based delivery requires a valid address.")
    @NotBlank(message = "Paper-based delivery requires a valid address.")
    private String streetAndNr;

    private int zipCode;

    @Size(max = MAX_FIELD_LENGTH, message = "Paper-based delivery requires a valid address.")
    @NotBlank(message = "Paper-based delivery requires a valid address.")
    private String city;
    private String cantonCodeSender;

    @AssertTrue(message = "Paper-based delivery requires a valid address.")
    public boolean isZipCodeInRange() {
        return ZIPCODE_VALUE_RANGE.contains(zipCode);
    }

    @AssertTrue(message = "Paper-based delivery requires a valid address.")
    public boolean isAllowedSender() {
        return AllowedSenders.isAccepted(this.cantonCodeSender);
    }
}
