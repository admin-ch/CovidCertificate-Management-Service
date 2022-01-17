package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.Constants;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import org.apache.commons.lang3.Range;

import java.time.LocalDate;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;

public interface IValidateVaccinationCertificateDataDto {

    String getMedicinalProductCode();

    Integer getNumberOfDoses();

    Integer getTotalNumberOfDoses();

    LocalDate getVaccinationDate();

    String getCountryOfVaccination();

    default void validate() {
        isMedicinalProductCodeValid();
        areDosesValid();
        isVaccinationDateValid();
        isCountryOfVaccinationValid();
    }

    private void isMedicinalProductCodeValid() {
        if (getMedicinalProductCode() == null || getMedicinalProductCode().isEmpty()) {
            throw new CreateCertificateException(INVALID_MEDICINAL_PRODUCT);
        }
    }

    private void areDosesValid() {
        Range<Integer> validDoseValueRange = Range.between(Constants.MIN_NB_OF_DOSES, Constants.MAX_NB_OF_DOSES);

        if (!validDoseValueRange.contains(getNumberOfDoses()) ||
                !validDoseValueRange.contains(getTotalNumberOfDoses()) ||
                (getNumberOfDoses() > getTotalNumberOfDoses() && getTotalNumberOfDoses() != 1)) {
            throw new CreateCertificateException(INVALID_DOSES);
        }
    }

    private void isVaccinationDateValid() {
        if (getVaccinationDate() == null || getVaccinationDate().isAfter(LocalDate.now())) {
            throw new CreateCertificateException(INVALID_VACCINATION_DATE);
        }
    }

    private void isCountryOfVaccinationValid() {
        if (getCountryOfVaccination() == null || getCountryOfVaccination().isEmpty()) {
            throw new CreateCertificateException(INVALID_COUNTRY_OF_VACCINATION);
        }
    }
}