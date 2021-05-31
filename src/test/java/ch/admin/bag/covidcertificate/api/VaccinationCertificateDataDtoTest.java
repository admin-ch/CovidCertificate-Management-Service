package ch.admin.bag.covidcertificate.api;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

public class VaccinationCertificateDataDtoTest {

    private final String medicinalProduct = "code";
    private final Integer numberOfDoses = 2;
    private final Integer totalNumberOfDoses = 2;
    private final LocalDate vaccinationDate = LocalDate.of(2021, Month.MAY, 4);
    private final String countryOfVaccination = "CH";

    @Test
    public void testInvalidDoses() {
        VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                null,
                totalNumberOfDoses,
                vaccinationDate,
                countryOfVaccination
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                null,
                vaccinationDate,
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                3,
                2,
                vaccinationDate,
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                -1,
                2,
                vaccinationDate,
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                10,
                8,
                vaccinationDate,
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                9,
                10,
                vaccinationDate,
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_DOSES, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryOfVaccination
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidVaccinationDate() {
        VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                null,
                countryOfVaccination
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_VACCINATION_DATE, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                LocalDate.now().plusDays(2),
                countryOfVaccination
        );
        exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_VACCINATION_DATE, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryOfVaccination
        );
        assertDoesNotThrow(testee::validate);
    }

    @Test
    public void testInvalidCountryOfVaccination() {
        VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                null
        );
        CreateCertificateException exception = assertThrows(CreateCertificateException.class, testee::validate);
        assertEquals(INVALID_COUNTRY_OF_VACCINATION, exception.getError());

        testee = new VaccinationCertificateDataDto(
                medicinalProduct,
                numberOfDoses,
                totalNumberOfDoses,
                vaccinationDate,
                countryOfVaccination
        );
        assertDoesNotThrow(testee::validate);
    }
}
