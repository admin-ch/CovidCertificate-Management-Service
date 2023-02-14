package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AntibodyCertificateDataDto {

    @JsonDeserialize(using = DateDeserializer.class)
    @NotNull(message = "Invalid sample date time! Sample date must be before current date time")
    private LocalDate sampleDate;
    private String testingCenterOrFacility;

    private static final LocalDate ANTIBODY_MIN_DATE = LocalDate.of(2021, 11, 16);

    @AssertFalse(message =  "Invalid testing center or facility")
    public boolean isTestCenterValid() {
        return !StringUtils.hasText(testingCenterOrFacility) || testingCenterOrFacility.length() > MAX_STRING_LENGTH;
    }

    @AssertFalse(message = "Invalid sample date time! Sample date must be before current date time")
    public boolean isSampleDateValid() {
        return sampleDate != null && sampleDate.isAfter(LocalDate.now());
    }

    @AssertFalse(message = "Date of sample collection must not be before 16.11.2021")
    public boolean isSampleDateBeforeDateValid() {
        return sampleDate != null && sampleDate.isBefore(ANTIBODY_MIN_DATE);
    }
}
