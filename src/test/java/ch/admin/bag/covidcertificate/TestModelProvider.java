package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.request.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

public class TestModelProvider {

    public static VaccinationCertificateCreateDto getVaccinationCertificateCreateDto(String medicalProductCode, String language) {
        return new VaccinationCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationCertificateDataDto(medicalProductCode)),
                language,
                getCovidCertificateAddressDto(),
                null
        );
    }

    public static VaccinationCertificateCreateDto getVaccinationCertificateCreateDto(String medicalProductCode, String language, String inAppCode) {
        return new VaccinationCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationCertificateDataDto(medicalProductCode)),
                language,
                null,
                inAppCode
        );
    }


    public static TestCertificateCreateDto getTestCertificateCreateDto(String typeCode, String manufacturerCode, String language) {
        return new TestCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getTestCertificateDataDto(typeCode, manufacturerCode)),
                language,
                null,
                null
        );
    }

    public static TestCertificateCreateDto getTestCertificateCreateDto(String typeCode, String manufacturerCode, String language, String inAppCode) {
        return new TestCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getTestCertificateDataDto(typeCode, manufacturerCode)),
                language,
                null,
                inAppCode
        );
    }

    public static RecoveryCertificateCreateDto getRecoveryCertificateCreateDto(String language) {
        return new RecoveryCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getRecoveryCertificateDataDto()),
                language,
                getCovidCertificateAddressDto(),
                null
        );
    }

    public static RecoveryCertificateCreateDto getRecoveryCertificateCreateDto(String language, String inAppCode) {
        return new RecoveryCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getRecoveryCertificateDataDto()),
                language,
                null,
                inAppCode
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
                "faimlyName",
                "givenName"
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

    public static CovidCertificateAddressDto getCovidCertificateAddressDto() {
        return new CovidCertificateAddressDto("street 12", 2500, "Bern", "BE");
    }
}
