package ch.admin.bag.covidcertificate.api.request;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CovidCertificatePersonNameDto {

    private static final String VALID_CHARACTERS_REGEX = "^[^!@#\\r\\n$%¶*\\\\()_:/+=|<>?{}\\[\\]~]+$";

    @NotNull(message = "Family name must not be null")
    @Pattern(regexp = VALID_CHARACTERS_REGEX, message = "Invalid family name! Must not contain any invalid chars")
    @Size(max = MAX_STRING_LENGTH, message = "Invalid family name! Must not exceed " + MAX_STRING_LENGTH + " chars")
    private String familyName;

    @NotNull(message = "Given name must not be null")
    @Pattern(regexp = VALID_CHARACTERS_REGEX, message = "Invalid given name! Must not contain any invalid chars")
    @Size(max = MAX_STRING_LENGTH, message = "Invalid given name! Must not exceed " + MAX_STRING_LENGTH + " chars")
    private String givenName;

    public CovidCertificatePersonNameDto(@Pattern(regexp = VALID_CHARACTERS_REGEX, message = "Invalid family name! Must not contain any invalid chars")String familyName,
                                         @Pattern(regexp = VALID_CHARACTERS_REGEX, message = "Invalid given name! Must not contain any invalid chars") String givenName) {
        if (familyName != null) {
            familyName = familyName.replaceAll("\\h", " ").trim();
            this.familyName = Jsoup.clean(familyName, Safelist.none());
        }
        if (givenName != null) {
            givenName = givenName.replaceAll("\\h", " ").trim();
            this.givenName = Jsoup.clean(givenName, Safelist.none());
        }
    }
}
