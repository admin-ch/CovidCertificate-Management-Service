package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.NO_ANTIBODY_DATA;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AntibodyCertificateCreateDto extends CertificateCreateDto {

    private List<AntibodyCertificateDataDto> antibodyInfo;

    public AntibodyCertificateCreateDto(
            CovidCertificatePersonDto personData,
            List<AntibodyCertificateDataDto> antibodyInfo,
            String language,
            CovidCertificateAddressDto address,
            String inAppDeliveryCode,
            SystemSource systemSource
    ) {
        super(personData, language, address, inAppDeliveryCode, systemSource);
        this.antibodyInfo = antibodyInfo;
    }

//    @Override
    public void validate() {
//        super.validate();
        if (antibodyInfo == null || antibodyInfo.isEmpty()) {
            throw new CreateCertificateException(NO_ANTIBODY_DATA);
        } else {
            antibodyInfo.forEach(
                    antibodyCertificateDataDto -> antibodyCertificateDataDto.validate(this.getSystemSource()));
        }

        if (antibodyInfo.stream().anyMatch(dto -> isBirthdateAfter(dto.getSampleDate()))) {
            throw new CreateCertificateException(DATE_OF_BIRTH_AFTER_CERTIFICATE_DATE);
        }
    }
}
