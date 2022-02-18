package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class RevocationDto {
    private static final String REGEX_UVCI = "^urn:uvci:01:CH:[A-Z0-9]{24}$";

    private String uvci;

    private boolean fraud;

    public void validate() {
        if (uvci == null || !uvci.matches(REGEX_UVCI)) {
            log.info("Validate revocation for {} failed.", uvci);
            throw new RevocationException(INVALID_UVCI);
        }
    }
}
