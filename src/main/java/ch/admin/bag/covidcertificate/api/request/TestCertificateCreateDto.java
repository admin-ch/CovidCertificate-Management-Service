package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_TEST_DATA;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestCertificateCreateDto extends CertificateCreateDto {

    private List<TestCertificateDataDto> testInfo;

    public TestCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<TestCertificateDataDto> testInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource

    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.testInfo = testInfo;
    }

    @Override
    public void validate() {
        super.validate();
        if (testInfo == null || testInfo.isEmpty()) {
            throw new CreateCertificateException(NO_TEST_DATA);
        } else {
            testInfo.forEach(TestCertificateDataDto::validate);
        }

        if (testInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getSampleDateTime()))) {
            throw new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE);
        }
    }

    @Override
    public boolean isDeliverablePerPost() {
        return false;
    }
}
