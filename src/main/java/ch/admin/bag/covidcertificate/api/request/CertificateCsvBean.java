package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateHelper;
import ch.admin.bag.covidcertificate.util.LuhnChecksum;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class CertificateCsvBean {

    @CsvBindByName(column = "givenName")
    private String givenName;
    @CsvBindByName(column = "familyName")
    private String familyName;
    @CsvBindByName(column = "dateOfBirth")
    private String dateOfBirth;
    @CsvBindByName(column = "language")
    private String language;
    @CsvBindByName(column = "streetAndNr")
    private String streetAndNr;
    @CsvBindByName(column = "zipCode")
    private String zipCode;
    @CsvBindByName(column = "city")
    private String city;
    @CsvBindByName(column = "cantonCodeSender")
    private String cantonCodeSender;
    @CsvBindByName(column = "error")
    private String error;
    @CsvBindByName(column = "inAppDeliveryCode")
    private String inAppDeliveryCode;

    public abstract CertificateCreateDto mapToCreateDto();

    public void setError(String error) {
        this.error = error;
    }

    protected VaccinationCertificateCreateDto mapToCreateDto(VaccinationCertificateDataDto dataDto) {
        return new VaccinationCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage().trim().toLowerCase(),
                mapToAddressDto(),
                validateAppDeliveryCode(),
                SystemSource.CsvUpload
        );
    }

    protected TestCertificateCreateDto mapToCreateDto(TestCertificateDataDto dataDto) {
        return new TestCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage().trim().toLowerCase(),
                mapToAddressDto(),
                validateAppDeliveryCode(),
                SystemSource.ApiGateway
        );
    }

    protected RecoveryCertificateCreateDto mapToCreateDto(RecoveryCertificateDataDto dataDto) {
        return new RecoveryCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage().trim().toLowerCase(),
                mapToAddressDto(),
                validateAppDeliveryCode(),
                SystemSource.WebUI
        );
    }

    private CovidCertificatePersonDto mapToPersonDto() {
        return new CovidCertificatePersonDto(
                new CovidCertificatePersonNameDto(
                        getFamilyName().trim(),
                        getGivenName().trim()
                ),
                getDateOfBirth()
        );
    }

    private CovidCertificateAddressDto mapToAddressDto() {
        if (StringUtils.hasText(streetAndNr) || StringUtils.hasText(zipCode) || StringUtils.hasText(city) || StringUtils.hasText(cantonCodeSender)) {
            int zipCodeTemp;
            try {
                zipCodeTemp = Integer.parseInt(this.zipCode);
            } catch (NumberFormatException e) {
                throw new CreateCertificateException(INVALID_ADDRESS);
            }
            return new CovidCertificateAddressDto(
                    streetAndNr.trim(),
                    zipCodeTemp,
                    city.trim(),
                    cantonCodeSender.trim().toUpperCase()
            );
        } else {
            return null;
        }
    }

    private String validateAppDeliveryCode() {
        if(inAppDeliveryCode == null || inAppDeliveryCode.equals("")){
                return null;
        } else if (inAppDeliveryCode.length() != 9) {
            throw new CreateCertificateException(INVALID_APP_CODE_LENGTH);
        } else if (!LuhnChecksum.validateCheckCharacter(inAppDeliveryCode)) {
            throw new CreateCertificateException(INVALID_APP_CODE_CHECKSUM);
        }
        return inAppDeliveryCode;
    }
}
