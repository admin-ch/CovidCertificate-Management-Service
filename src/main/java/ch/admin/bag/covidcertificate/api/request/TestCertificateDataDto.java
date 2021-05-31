package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.*;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestCertificateDataDto {

    private String manufacturerCode;

    private String typeCode;

    private ZonedDateTime sampleDateTime;

    private String testingCentreOrFacility;

    private String memberStateOfTest;

    public void validate(){
        if (sampleDateTime == null ||
                sampleDateTime.isAfter(ZonedDateTime.now(sampleDateTime.getZone()))) {
            throw new CreateCertificateException(INVALID_SAMPLE_OR_RESULT_DATE_TIME);
        }
        if (!StringUtils.hasText(testingCentreOrFacility) || testingCentreOrFacility.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_TEST_CENTER);
        }
        if (memberStateOfTest == null) {
            throw new CreateCertificateException(INVALID_MEMBER_STATE_OF_TEST);
        }
    }
}
