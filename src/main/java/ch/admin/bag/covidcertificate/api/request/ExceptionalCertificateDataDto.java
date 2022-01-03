package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator;
import ch.admin.bag.covidcertificate.api.request.validator.TextValidator;
import ch.admin.bag.covidcertificate.util.DateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TEST_CENTER;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_STRING_LENGTH;
import static ch.admin.bag.covidcertificate.api.request.validator.LocalDateValidator.*;
import static ch.admin.bag.covidcertificate.api.request.validator.TextValidator.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExceptionalCertificateDataDto {

    private static final LocalDate LIMIT_MIN_DATE = LocalDate.of(2021, 10, 1);
    @JsonDeserialize(using = DateDeserializer.class)
    private LocalDate validFrom;
    private String attestationIssuer;

    public void validate(SystemSource systemSource) {
        validateDateIsNotBeforeLimitDate(validFrom, "validFrom", LIMIT_MIN_DATE);
        validateTextIsNotBlankAndLengthIsNotBiggerThanMaxLength(attestationIssuer,"attestationIssuer",MAX_STRING_LENGTH);

        switch (systemSource) {
            case WebUI:
            case CsvUpload: {
                break;
            }
            case ApiGateway:
            case ApiPlatform: {
                throw new AccessDeniedException("Exceptional certificates can't be generated through the ApiPlatform or the ApitGateway.");
            }
            default:
                throw new IllegalStateException("Attribute systemSource is invalid. Check Request implementation and/or Dto Validation.");
        }
    }
}
