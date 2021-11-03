package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AntibodyCertificateDataDto {

    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate sampleDate;
    private String testingCentreOrFacility;

    public void validate(SystemSource systemSource) {
        if (sampleDate == null || sampleDate.isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_SAMPLE_DATE_TIME);
        }
        if (!StringUtils.hasText(testingCentreOrFacility) || testingCentreOrFacility.length() > MAX_STRING_LENGTH) {
            throw new CreateCertificateException(INVALID_TEST_CENTER);
        }

        switch (systemSource) {
            case WebUI:
            case CsvUpload:
            case ApiGateway: {
                break;
            }
            case ApiPlatform: {
                throw new AccessDeniedException("Antibody certificates can't be generated through the ApiPlatform.");
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
    }
}
