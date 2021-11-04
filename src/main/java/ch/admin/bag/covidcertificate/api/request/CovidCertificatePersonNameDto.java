package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonNameDto {

    private static final Pattern p = Pattern.compile("[!@#\\r\\n$%¶*\\\\()_:/+=|<>?{}\\[\\]~-]");

    private String familyName;

    private String givenName;

    public void validate() {
        if (!StringUtils.hasText(givenName) || givenName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }
        if (!StringUtils.hasText(familyName) || familyName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }

        Matcher givenNameMatcher = p.matcher(givenName);
        if(givenNameMatcher.find()) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }

        Matcher familyNameMatcher = p.matcher(familyName);
        if(familyNameMatcher.find()) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }

        familyName = Jsoup.clean(familyName, Safelist.none());
        givenName = Jsoup.clean(givenName, Safelist.none());
    }
}
