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

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.ISO_3166_1_ALPHA_2_CODE_SWITZERLAND;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.*;
import static ch.admin.bag.covidcertificate.api.request.validator.TextValidator.validateTextLengthIsNotBiggerThanMaxLength;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecoveryRatCertificateDataDto extends BaseTestCertificateDataDto {

    // if this needs to be changed, change assertion message in isDateNotBeforeLimitDate accordingly.
    private static final LocalDate LIMIT_MIN_DATE = LocalDate.of(2021, 10, 1);

    @AssertTrue(message = "Sample date may not be before 2021-10-1")
    public boolean isDateNotBeforeLimitDate() {

        if (getSampleDateTime() == null) return true;
        return isDateNotBeforeTheLimitDate(getSampleDateTime().toLocalDate(), LIMIT_MIN_DATE);
    }

    @AssertTrue(message =  "Invalid member state of test")
    public boolean isMemberStateOfTestValid() {
        return ISO_3166_1_ALPHA_2_CODE_SWITZERLAND.equals(getMemberStateOfTest());
    }

    public RecoveryRatCertificateDataDto(ZonedDateTime sampleDateTime, String testingCentreOrFacility, String memberStateOfTest) {
        super(sampleDateTime, testingCentreOrFacility, memberStateOfTest);
    }
}
