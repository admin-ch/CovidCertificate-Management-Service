package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonNameDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

public class TestModelProvider {

    public static VaccinationCertificateCreateDto getVaccinationCertificateCreateDto(
            String medicalProductCode, String language) {
        return new VaccinationCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationCertificateDataDto(medicalProductCode)),
                language,
                getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
    }

    public static VaccinationCertificateCreateDto getVaccinationCertificateCreateDto(
            String medicalProductCode, String language, String inAppCode) {
        return new VaccinationCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationCertificateDataDto(medicalProductCode)),
                language,
                null,
                inAppCode,
                SystemSource.WebUI
        );
    }

    public static TestCertificateCreateDto getTestCertificateCreateDto(
            String typeCode, String manufacturerCode, String language) {
        return new TestCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getTestCertificateDataDto(typeCode, manufacturerCode)),
                language,
                null,
                null,
                SystemSource.WebUI
        );
    }

    public static TestCertificateCreateDto getTestCertificateCreateDto(
            String typeCode, String manufacturerCode, String language, String inAppCode) {
        return new TestCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getTestCertificateDataDto(typeCode, manufacturerCode)),
                language,
                null,
                inAppCode,
                SystemSource.WebUI
        );
    }

    public static String getVaccinationCertificateJSONWithInvalidVaccinationDate() {
        return "{\"name\":{\"familyName\":\"2eaf462f-3b6a-4088-9892-fe4461dda8a3\",\"givenName\":\"1c2828d9-9ed3-4f60-b843-4d4b4eec9dc9\"},\"dateOfBirth\":\"0c511a7c-dcc7-42d1-848c-b88159f036a4\",\"language\":\"de\",\"address\":{\"streetAndNr\":\"b8463a54-d9c9-4594-b2df-efafa4926c79\",\"zipCode\":86,\"city\":\"04cab8fc-0beb-4011-8109-e6a0e034c307\",\"cantonCodeSender\":\"3bedd305-9301-48e3-9473-26e8188b01f6\"},\"appCode\":\"DB1E7078-1E31-46FD-8605-F67105907D46\",\"systemSource\":\"ApiGateway\",\"vaccinationInfo\":[{\"medicinalProductCode\":\"7dc3e1c9-9ca9-4180-b407-0741549ba898\",\"numberOfDoses\":4,\"totalNumberOfDoses\":6,\"vaccinationDate\":\"2010--0202\",\"countryOfVaccination\":\"16ebd49a-de13-407b-984e-7e2cb9611dd5\"},{\"medicinalProductCode\":\"8b306ac9-1f87-4b09-9a3c-d38a211f14f8\",\"numberOfDoses\":7,\"totalNumberOfDoses\":8,\"vaccinationDate\":[2021,10,15],\"countryOfVaccination\":\"ea06cb48-e2cc-4a53-9f86-6e7221734728\"},{\"medicinalProductCode\":\"aedecc6e-a6ca-4149-8d72-e0d188f8d66d\",\"numberOfDoses\":7,\"totalNumberOfDoses\":8,\"vaccinationDate\":[2021,10,15],\"countryOfVaccination\":\"dfa035fc-21f0-447b-b8ee-ddcc651c8cd3\"}]}";
    }

    public static String getRecoveryCertificateCreateJSONWithInvalidPositiveTestResultDate() {
        return "{\"name\":{\"familyName\":\"faimlyName\",\"givenName\":\"givenName\"},\"dateOfBirth\":\"1990-05-13\",\"language\":\"de\",\"address\":null,\"appCode\":\"null\",\"systemSource\":\"WebUI\",\"recoveryInfo\":[{\"dateOfFirstPositiveTestResult\":\"2010--0505\",\"countryOfTest\":\"CH\"}]}";
    }

    public static String getTestCertificateCreateDtoJSONWithInvalidSampleDateTime() {
        return "{\"name\":{\"familyName\":\"faimlyName\",\"givenName\":\"givenName\"},\"dateOfBirth\":\"1990-05-13\",\"language\":\"de\",\"address\":null,\"appCode\":\"null\",\"systemSource\":\"WebUI\",\"testInfo\":[{\"manufacturerCode\":\"1833\",\"typeCode\":\"null\",\"sampleDateTime\":\"2010--0205\",\"testingCentreOrFacility\":\"Test Center\",\"memberStateOfTest\":\"CH\"}]}";
    }

    public static RecoveryCertificateCreateDto getRecoveryCertificateCreateDto(String language) {
        return new RecoveryCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getRecoveryCertificateDataDto()),
                language,
                getCovidCertificateAddressDto(),
                null,
                SystemSource.WebUI
        );
    }

    public static RecoveryCertificateCreateDto getRecoveryCertificateCreateDto(String language, String inAppCode) {
        return new RecoveryCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getRecoveryCertificateDataDto()),
                language,
                null,
                inAppCode,
                SystemSource.WebUI
        );
    }

    public static AntibodyCertificateCreateDto getAntibodyCertificateCreateDto(String language) {
        return new AntibodyCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getAntibodyCertificateDataDto()),
                language,
                null,
                null,
                SystemSource.WebUI
        );
    }

    public static VaccinationTouristCertificateCreateDto getVaccinationTouristCertificateCreateDto(
            String medicalProductCode, String language) {
        return new VaccinationTouristCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getVaccinationTouristCertificateDataDto(medicalProductCode)),
                language,
                null,
                null,
                SystemSource.WebUI
        );
    }

    public static ExceptionalCertificateCreateDto getExceptionalCertificateCreateDto(String language) {
        return new ExceptionalCertificateCreateDto(
                getCovidCertificatePersonDto(),
                List.of(getExceptionalCertificateDataDto()),
                language,
                null,
                null,
                SystemSource.WebUI
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
                LocalDate.of(1990, Month.MAY, 13).toString()
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

    public static AntibodyCertificateDataDto getAntibodyCertificateDataDto() {
        return new AntibodyCertificateDataDto(
                LocalDate.of(2021, Month.APRIL, 4),
                "Test Center"

        );
    }

    public static VaccinationTouristCertificateDataDto getVaccinationTouristCertificateDataDto(
            String medicalProductCode) {
        return new VaccinationTouristCertificateDataDto(
                medicalProductCode,
                2,
                2,
                LocalDate.of(2021, Month.APRIL, 29),
                "CH"
        );
    }

    public static ExceptionalCertificateDataDto getExceptionalCertificateDataDto() {
        return new ExceptionalCertificateDataDto(
                LocalDate.of(2021, Month.APRIL, 29),
                "Testing center"
        );
    }

    public static CovidCertificateAddressDto getCovidCertificateAddressDto() {
        return new CovidCertificateAddressDto("street 12", 2500, "Bern", "BE");
    }
}
