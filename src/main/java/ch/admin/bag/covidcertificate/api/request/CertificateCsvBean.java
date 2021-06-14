package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_ADDRESS;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DATE_OF_BIRTH;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class CertificateCsvBean {

    @CsvBindByName(column = "id")
    private String id;
    @CsvBindByName(column = "givenName")
    private String givenName;
    @CsvBindByName(column = "familyName")
    private String familyName;
    @CsvBindByName(column = "dateOfBirth")
    private String dateOfBirth;
    @CsvBindByName(column = "language")
    private String language;
    @CsvBindByName(column = "addressLine1")
    private String addressLine1;
    @CsvBindByName(column = "addressLine2")
    private String addressLine2;
    @CsvBindByName(column = "zipCode")
    private String zipCode;
    @CsvBindByName(column = "city")
    private String city;
    @CsvBindByName(column = "cantonCode")
    private String cantonCode;
    @CsvBindByName(column = "isGeneratePDF")
    private String isGeneratePDF;

    public abstract CertificateCreateDto mapToCreateDto();

    protected VaccinationCertificateCreateDto mapToCreateDto(VaccinationCertificateDataDto dataDto) {
        return new VaccinationCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage(),
                mapToAddressDto()
        );
    }

    protected TestCertificateCreateDto mapToCreateDto(TestCertificateDataDto dataDto) {
        return new TestCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage(),
                mapToAddressDto()
        );
    }

    protected RecoveryCertificateCreateDto mapToCreateDto(RecoveryCertificateDataDto dataDto) {
        return new RecoveryCertificateCreateDto(
                mapToPersonDto(),
                List.of(dataDto),
                getLanguage(),
                mapToAddressDto()
        );
    }

    private CovidCertificatePersonDto mapToPersonDto() {
        LocalDate birthDate;
        try {
            birthDate = LocalDate.parse(getDateOfBirth());
        } catch (Exception e) {
            throw new CreateCertificateException(INVALID_DATE_OF_BIRTH);
        }
        return new CovidCertificatePersonDto(
                new CovidCertificatePersonNameDto(
                        getFamilyName(),
                        getGivenName()
                ),
                birthDate
        );
    }

    private CovidCertificateAddressDto mapToAddressDto() {
        int zipCode;
        try {
            zipCode = Integer.parseInt(this.zipCode);
        } catch (NumberFormatException e) {
            throw new CreateCertificateException(INVALID_ADDRESS);
        }
        return new CovidCertificateAddressDto(
                addressLine2,
                zipCode,
                city,
                cantonCode
        );
    }
}
