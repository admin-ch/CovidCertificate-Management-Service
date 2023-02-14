package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class BaseTestCertificateDataDto {

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @NotNull(message = "Invalid sample date time! Sample date must be before current date time")
    private ZonedDateTime sampleDateTime;

    private String testingCentreOrFacility;

    @NotNull(message = "Invalid member state of test")
    private String memberStateOfTest;

    @AssertFalse(message = "Invalid sample date time! Sample date must be before current date time")
    public boolean isSampleDateTimeValid() {
        return sampleDateTime != null && sampleDateTime.toLocalDate().isAfter(LocalDate.now());
    }

    @AssertFalse(message =  "Invalid testing center or facility")
    public boolean isTestCenterValid() {
        return !StringUtils.hasText(testingCentreOrFacility) || testingCentreOrFacility.length() > MAX_STRING_LENGTH;
    }
}
