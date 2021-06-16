package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.request.*;

import java.time.*;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

public class TestModelProvider {

    public static VaccinationCertificateCreateDto getVaccinationCertificateCreateDto(String medicalProductCode) {
        return new VaccinationCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationCertificateDataDto(medicalProductCode)),
                "de"
        );
    }

    public static TestCertificateCreateDto getTestCertificateCreateDto(
            String typeCode,
            String manufacturerCode
    ) {
        return new TestCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getTestCertificateDataDto(typeCode, manufacturerCode)),
                "de"
        );
    }

    public static RecoveryCertificateCreateDto getRecoveryCertificateCreateDto() {
        return new RecoveryCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getRecoveryCertificateDataDto()),
                "de"
        );
    }

    public static RecoveryCertificateDataDto getRecoveryCertificateDataDto() {
        return new RecoveryCertificateDataDto(
                LocalDate.of(2021, Month.MAY, 2),
                "CH"
        );
    }

    public static CovidCertificatePersonDto getCovidCertificatePersonDto() {
        return new CovidCertificatePersonDto(
                getCovidCertificatePersonNameDto(),
                LocalDate.of(1990, Month.MAY, 13)
        );
    }

    public static CovidCertificatePersonNameDto getCovidCertificatePersonNameDto() {
        return new CovidCertificatePersonNameDto(
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM",
                "MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM"
        );
    }

    public static VaccinationCertificateDataDto getVaccinationCertificateDataDto(String medicalProductCode) {
        return new VaccinationCertificateDataDto(
                medicalProductCode,
                2,
                2,
                LocalDate.of(2021, Month.APRIL, 29),
                "CH"
        );
    }

    public static TestCertificateDataDto getTestCertificateDataDto(
            String typeCode,
            String manufacturerCode
    ) {
        return new TestCertificateDataDto(
                manufacturerCode,
                typeCode,
                ZonedDateTime.of(LocalDateTime.of(2021, Month.APRIL, 4, 16, 25, 12, 354), SWISS_TIMEZONE),
                "Test Center",
                "CH"
        );
    }
}
