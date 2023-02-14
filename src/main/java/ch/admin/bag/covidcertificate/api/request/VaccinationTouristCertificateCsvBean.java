package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.util.DateHelper;
import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationTouristCertificateCsvBean extends CertificateCreateCsvBean {

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
        int nbOfDoses;
        int totalNbOfDoses;
        try {
            nbOfDoses = Integer.parseInt(this.numberOfDoses);
            totalNbOfDoses = Integer.parseInt(this.totalNumberOfDoses);
        } catch (NumberFormatException e) {
            throw new CreateCertificateException(INVALID_DOSES);
        }
        var dataDto = new VaccinationCertificateDataDto(
                medicinalProductCode != null ? medicinalProductCode.trim().toUpperCase() : null,
                nbOfDoses,
                totalNbOfDoses,
                DateHelper.parse(this.vaccinationDate, INVALID_VACCINATION_DATE),
                countryOfVaccination.trim().toUpperCase()
        );
        return super.mapToCreateDto(dataDto);
    }
}
