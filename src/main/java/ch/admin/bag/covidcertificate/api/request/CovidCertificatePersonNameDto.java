package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_FAMILY_NAME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_GIVEN_NAME;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CovidCertificatePersonNameDto {

    private static final Pattern invalidCharactersRegex = Pattern.compile("[!@#\\r\\n$%¶*\\\\()_:/+=|<>?{}\\[\\]~]");

    private String familyName;

    private String givenName;

    public void validate() {
        if (familyName == null) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }

        if (givenName == null) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }

        Matcher givenNameMatcher = invalidCharactersRegex.matcher(givenName);
        if(givenNameMatcher.find()) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }

        Matcher familyNameMatcher = invalidCharactersRegex.matcher(familyName);
        if(familyNameMatcher.find()) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }

        familyName = familyName.replaceAll("\\h", " ").trim();
        givenName = givenName.replaceAll("\\h", " ").trim();

        if (!StringUtils.hasText(givenName) || givenName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_GIVEN_NAME);
        }
        if (!StringUtils.hasText(familyName) || familyName.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_FAMILY_NAME);
        }

        familyName = Jsoup.clean(familyName, Safelist.none());
        givenName = Jsoup.clean(givenName, Safelist.none());
    }
}
