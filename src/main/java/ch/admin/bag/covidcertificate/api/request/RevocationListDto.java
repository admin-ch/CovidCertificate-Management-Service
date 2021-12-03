package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.RevocationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_UVCI;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
public class RevocationListDto {
    private static final String REGEX_UVCI = "^urn:uvci:01:CH:[A-Z0-9]{24}$";

    private List<String> uvcis;

    public void validateList() {
        for (String uvci : uvcis) {
            if (!uvci.matches(REGEX_UVCI)) {
                log.info("Validate revocation for {} failed.", uvci);
                throw new RevocationException(INVALID_UVCI);
            }
        }
    }
}