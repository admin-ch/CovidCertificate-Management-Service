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
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEMBER_STATE_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_SAMPLE_DATE_TIME;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TEST_CENTER;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestCertificateDataDto {

    private String manufacturerCode;

    private String typeCode;

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime sampleDateTime;

    private String testingCentreOrFacility;

    private String memberStateOfTest;

    public void validate(){
        if (sampleDateTime == null ||
                sampleDateTime.toLocalDate().isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
        }
        if (!StringUtils.hasText(testingCentreOrFacility) || testingCentreOrFacility.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_TEST_CENTER);
        }
        if (memberStateOfTest == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }
    }
}
