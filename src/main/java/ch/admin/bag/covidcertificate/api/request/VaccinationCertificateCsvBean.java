package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationCertificateCsvBean extends CertificateCsvBean {

    @CsvBindByName(column = "medicinalProductCode")
    private String medicinalProductCode;
    @CsvBindByName(column = "numberOfDoses")
    private String numberOfDoses;
    @CsvBindByName(column = "totalNumberOfDoses")
    private String totalNumberOfDoses;
    @CsvBindByName(column = "vaccinationDate")
    private String vaccinationDate;
    @CsvBindByName(column = "countryOfVaccination")
    private String countryOfVaccination;

    @Override
    public VaccinationCertificateCreateDto mapToCreateDto() {
        int numberOfDoses;
        int totalNumberOfDoses;
        try {
            numberOfDoses = Integer.parseInt(this.numberOfDoses);
            totalNumberOfDoses = Integer.parseInt(this.totalNumberOfDoses);
        } catch (NumberFormatException e) {
            throw new CreateCertificateException(INVALID_DOSES);
        }
        LocalDate vaccinationDate;
        try {
            vaccinationDate = LocalDate.parse(this.vaccinationDate);
        } catch (Exception e) {
            throw new CreateCertificateException(INVALID_VACCINATION_DATE);
        }
        VaccinationCertificateDataDto dataDto = new VaccinationCertificateDataDto(
                medicinalProductCode,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryOfVaccination
        );
        return super.mapToCreateDto(dataDto);
    }
}
