package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.AccessDeniedException;

import javax.validation.constraints.AssertTrue;
import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.isDateNotBeforeTheLimitDate;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.isDateNotInTheFuture;
import static ch.admin.bag.covidcertificate.api.request.validator.TextValidator.validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExceptionalCertificateDataDto {

    // if this needs to be changed, change assertion message in isDateNotBeforeLimitDate accordingly.
    private static final LocalDate LIMIT_MIN_DATE = LocalDate.of(2021, 10, 1);
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate validFrom;
    private String attestationIssuer;

    @AssertTrue(message = "validFrom may not be in the future")
    public boolean isValidFromNotInFuture() {
      return isDateNotInTheFuture(validFrom);
    }

    @AssertTrue(message = "validFrom may not be before 2021-10-1")
    public boolean isValidFromNotBeforeMinDate() {
        return isDateNotBeforeTheLimitDate(validFrom, LIMIT_MIN_DATE);
    }

    @AssertTrue(message = "attestationIssuer may not be blank and not exceed " + MAX_STRING_LENGTH + " characters")
    public boolean isValidAttestationIssuer() {
        return attestationIssuer != null && validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(attestationIssuer, MAX_STRING_LENGTH);
    }
}
