package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_FRAUD_FLAG;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class UvciForRevocationDto {
    private String uvci;
    private Boolean fraud;

    public void validate() {
        if (fraud == null) {
            throw new RevocationException(INVALID_FRAUD_FLAG);
        }
    }
}