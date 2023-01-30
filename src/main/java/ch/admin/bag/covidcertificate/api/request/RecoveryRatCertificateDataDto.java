package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.validateDateIsNotBeforeLimitDate;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.validateDateIsNotInTheFuture;
import static ch.admin.bag.covidcertificate.api.request.validator.TextValidator.validateTextLengthIsNotBiggerThanMaxLength;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecoveryRatCertificateDataDto {

    private static final LocalDate LIMIT_MIN_DATE = LocalDate.of(2021, 10, 1);

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime sampleDateTime;

    private String testingCentreOrFacility;

    private String memberStateOfTest;

    public void validate() {
        LocalDate sampleDate = sampleDateTime.toLocalDate();
        validateDateIsNotInTheFuture(sampleDate, "sampleDateTime");
        validateDateIsNotBeforeLimitDate(sampleDate, "sampleDateTime", LIMIT_MIN_DATE);
        validateTextLengthIsNotBiggerThanMaxLength(testingCentreOrFacility, "testingCentreOrFacility", MAX_STRING_LENGTH);

        if (!ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equals(memberStateOfTest)) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }
    }
}
