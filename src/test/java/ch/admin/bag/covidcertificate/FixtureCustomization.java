package ch.admin.bag.covidcertificate;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.AntibodyCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.CovidCertificateAddressDto;
import ch.admin.bag.covidcertificate.api.request.ExceptionalCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RecoveryRatCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.RevocationDto;
import ch.admin.bag.covidcertificate.api.request.RevocationListDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.TestCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.UvciForRevocationDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDto;
import ch.admin.bag.covidcertificate.api.request.conversion.VaccinationCertificateConversionRequestDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.CertificatePersonNameDto;
import ch.admin.bag.covidcertificate.api.request.pdfgeneration.VaccinationCertificateHcertDecodedDto;
import ch.admin.bag.covidcertificate.api.response.CovidCertificateCreateResponseDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.IssuableVaccineDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.enums.Issuable;
import ch.admin.bag.covidcertificate.service.domain.pdf.AntibodyCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.RecoveryCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.TestCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationCertificatePdf;
import ch.admin.bag.covidcertificate.service.domain.pdf.VaccinationTouristCertificatePdf;
import com.flextrade.jfixture.JFixture;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.DE;

public class FixtureCustomization {

    public static void customizeIssuableVaccineDto(JFixture fixture) {
        fixture.customise().lazyInstance(IssuableVaccineDto.class, () -> new IssuableVaccineDto(
                fixture.create(String.class), fixture.create(String.class),
                fixture.create(String.class), fixture.create(String.class),
                fixture.create(String.class), fixture.create(String.class),
                fixture.create(Issuable.class), fixture.create(Boolean.class)
        ));
    }

    public static void customizeTestValueSet(JFixture fixture) {
        fixture.customise().lazyInstance(IssuableTestDto.class, () -> new IssuableTestDto(fixture.create(String.class), fixture.create(String.class), fixture.create(TestType.class), fixture.create(ZonedDateTime.class)));
    }

    public static void customizeCountryCode(JFixture fixture) {
        fixture.customise().lazyInstance(CountryCode.class, () -> {
            var countryCode = new CountryCode();
            ReflectionTestUtils.setField(countryCode, "shortName", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "display", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "lang", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "active", fixture.create(Boolean.class));
            ReflectionTestUtils.setField(countryCode, "version", fixture.create(String.class));
            ReflectionTestUtils.setField(countryCode, "system", fixture.create(String.class));
            return countryCode;
        });
    }

    public static void customizeVaccine(JFixture fixture) {
        fixture.customise().lazyInstance(Vaccine.class, () -> {
            var vaccine = Vaccine.builder()
                    .code(fixture.create(String.class))
                    .display(fixture.create(String.class))
                    .active(fixture.create(Boolean.class))
                    .createdAt(fixture.create(LocalDateTime.class))
                    .modifiedAt(fixture.create(LocalDateTime.class))
                    .build();
            return vaccine;
        });
    }

    public static void customizeRapidTest(JFixture fixture) {
        fixture.customise().lazyInstance(RapidTest.class, () -> {
            return new RapidTest(
                    fixture.create(String.class),
                    fixture.create(String.class),
                    fixture.create(Boolean.class),
                    fixture.create(LocalDateTime.class),
                    null
            );
        });
    }

    public static void customizeVaccinationCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            customizeVaccinationCertificateDataDto(helperFixture);
            var vaccinationCertificateCreateDto = helperFixture.create(VaccinationCertificateCreateDto.class);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "language", DE);
            return vaccinationCertificateCreateDto;
        });
    }

    public static void customizeCovidCertificateAddressDto(JFixture fixture, CertificateCreateDto createDto, String field, Object value) {
        fixture.customise().lazyInstance(CovidCertificateAddressDto.class, () -> {
            var covidCertificateAddressDto = new JFixture().create(CovidCertificateAddressDto.class);
            ReflectionTestUtils.setField(covidCertificateAddressDto, field, value);
            return covidCertificateAddressDto;
        });
        ReflectionTestUtils.setField(createDto, "address", fixture.create(CovidCertificateAddressDto.class));
    }

    private static void customizeVaccinationCertificateDataDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationCertificateDataDto.class, () -> {
            var numberOfDoses = fixture.create(Integer.class) % 9 + 1;
            var totalNumberOfDoses = numberOfDoses + (int) Math.ceil(Math.random() * (9 - numberOfDoses));
            var vaccinationCertificateCreateDto = new JFixture().create(VaccinationCertificateDataDto.class);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "numberOfDoses", numberOfDoses);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "totalNumberOfDoses", totalNumberOfDoses);
            return vaccinationCertificateCreateDto;
        });
    }

    public static void customizeTestCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(TestCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var testCertificateCreateDto = helperFixture.create(TestCertificateCreateDto.class);
            ReflectionTestUtils.setField(testCertificateCreateDto, "language", DE);
            return testCertificateCreateDto;
        });
    }

    public static void customizeRecoveryCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(RecoveryCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var recoveryCertificateCreateDto = helperFixture.create(RecoveryCertificateCreateDto.class);
            ReflectionTestUtils.setField(recoveryCertificateCreateDto, "language", DE);
            return recoveryCertificateCreateDto;
        });
    }

    public static void customizeRecoveryRatCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(RecoveryRatCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var recoveryRatCertificateCreateDto = helperFixture.create(RecoveryRatCertificateCreateDto.class);
            ReflectionTestUtils.setField(recoveryRatCertificateCreateDto, "language", DE);
            return recoveryRatCertificateCreateDto;
        });
    }

    public static void customizeVaccinationTouristCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationTouristCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            customizeVaccinationTouristCertificateDataDto(helperFixture);
            var vaccinationTouristCertificateCreateDto = helperFixture.create(
                    VaccinationTouristCertificateCreateDto.class);
            ReflectionTestUtils.setField(vaccinationTouristCertificateCreateDto, "language", DE);
            return vaccinationTouristCertificateCreateDto;
        });
    }

    private static void customizeVaccinationTouristCertificateDataDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationTouristCertificateDataDto.class, () -> {
            var numberOfDoses = fixture.create(Integer.class) % 9 + 1;
            var totalNumberOfDoses = numberOfDoses + (int) Math.ceil(Math.random() * (9 - numberOfDoses));
            var vaccinationTouristCertificateCreateDto = new JFixture().create(VaccinationTouristCertificateDataDto.class);
            ReflectionTestUtils.setField(vaccinationTouristCertificateCreateDto, "numberOfDoses", numberOfDoses);
            ReflectionTestUtils.setField(vaccinationTouristCertificateCreateDto, "totalNumberOfDoses", totalNumberOfDoses);
            return vaccinationTouristCertificateCreateDto;
        });
    }

    public static void customizeAntibodyCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(AntibodyCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var antibodyCertificateCreateDto = helperFixture.create(AntibodyCertificateCreateDto.class);
            ReflectionTestUtils.setField(antibodyCertificateCreateDto, "language", DE);
            return antibodyCertificateCreateDto;
        });
    }

    public static void customizeExceptionalCertificateCreateDto(JFixture fixture) {
        fixture.customise().lazyInstance(ExceptionalCertificateCreateDto.class, () -> {
            var helperFixture = new JFixture();
            var exceptionalCertificateCreateDto = helperFixture.create(ExceptionalCertificateCreateDto.class);
            ReflectionTestUtils.setField(exceptionalCertificateCreateDto, "language", DE);
            return exceptionalCertificateCreateDto;
        });
    }

    public static void customizeCreateCertificateException(JFixture fixture) {
        fixture.customise().lazyInstance(CreateCertificateException.class, () -> {
            var createCertificateError = fixture.create(CreateCertificateError.class);
            return new CreateCertificateException(createCertificateError);
        });
    }

    public static void customizeRevocationDto(JFixture fixture, boolean fraud) {
        fixture.customise().lazyInstance(RevocationDto.class, () -> new RevocationDto(createUVCI(), fixture.create(SystemSource.class), null, fraud));
    }

    public static void customizeUvciForRevocationDto(JFixture fixture, boolean fraud) {
        fixture.customise().lazyInstance(UvciForRevocationDto.class, () -> new UvciForRevocationDto(createUVCI(), fraud));
    }

    public static void customizeRevocationListDto(JFixture fixture) {
        List<UvciForRevocationDto> uvcis = new LinkedList<>();
        uvcis.add(new UvciForRevocationDto(createUVCI(), false));
        uvcis.add(new UvciForRevocationDto(createUVCI(), false));
        uvcis.add(new UvciForRevocationDto(createUVCI(), false));
        fixture.customise().lazyInstance(RevocationListDto.class, () -> new RevocationListDto(uvcis, fixture.create(SystemSource.class), fixture.create(String.class)));
    }

    public static String createUVCI() {
        return "urn:uvci:01:CH:" + RandomStringUtils.randomAlphanumeric(24).toUpperCase();
    }

    public static void customizeCovidCertificateCreateResponseDto(JFixture fixture) {
        fixture.customise().lazyInstance(CovidCertificateCreateResponseDto.class, () -> {
            var helperFixture = new JFixture();
            var covidCertificateCreateResponseDto = helperFixture.create(CovidCertificateCreateResponseDto.class);
            ReflectionTestUtils.setField(covidCertificateCreateResponseDto, "appDeliveryError", null);
            return covidCertificateCreateResponseDto;
        });
    }

    public static void customizeVaccinationCertificatePdf(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationCertificatePdf.class, () -> {
            var helperFixture = new JFixture();
            var pdf = helperFixture.create(VaccinationCertificatePdf.class);
            ReflectionTestUtils.setField(pdf, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            return pdf;
        });
    }

    public static void customizeTestCertificatePdf(JFixture fixture) {
        fixture.customise().lazyInstance(TestCertificatePdf.class, () -> {
            var helperFixture = new JFixture();
            var pdf = helperFixture.create(TestCertificatePdf.class);
            ReflectionTestUtils.setField(pdf, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            return pdf;
        });
    }

    public static void customizeRecoveryCertificatePdf(JFixture fixture) {
        fixture.customise().lazyInstance(RecoveryCertificatePdf.class, () -> {
            var helperFixture = new JFixture();
            var pdf = helperFixture.create(RecoveryCertificatePdf.class);
            ReflectionTestUtils.setField(pdf, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            return pdf;
        });
    }

    public static void customizeAntibodyCertificatePdf(JFixture fixture) {
        fixture.customise().lazyInstance(AntibodyCertificatePdf.class, () -> {
            var helperFixture = new JFixture();
            var pdf = helperFixture.create(AntibodyCertificatePdf.class);
            ReflectionTestUtils.setField(pdf, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            return pdf;
        });
    }

    public static void customizeVaccinationTouristCertificatePdf(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationTouristCertificatePdf.class, () -> {
            var helperFixture = new JFixture();
            var pdf = helperFixture.create(VaccinationTouristCertificatePdf.class);
            ReflectionTestUtils.setField(pdf, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            return pdf;
        });
    }

    public static void customizeVaccinationCertificateConversionRequestDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationCertificateConversionRequestDto.class, () -> {
            var helperFixture = new JFixture();
            var vaccinationCertificateConversionRequestDto = helperFixture.create(
                    VaccinationCertificateConversionRequestDto.class);
            customizeVaccinationCertificateDataDto(helperFixture);
            var vaccinationCertificateCreateDto = helperFixture.create(VaccinationCertificateCreateDto.class);
            ReflectionTestUtils.setField(vaccinationCertificateCreateDto, "language", DE);
            return vaccinationCertificateCreateDto;
        });
    }

    private static void customizeVaccinationCertificateHcertDecodedDto(JFixture fixture) {
        fixture.customise().lazyInstance(VaccinationCertificateHcertDecodedDto.class, () -> {
            var vaccinationCertificateHcertDecodedDto = new JFixture().create(
                    VaccinationCertificateHcertDecodedDto.class);
            var numberOfDoses = fixture.create(Integer.class) % 9 + 1;
            var totalNumberOfDoses = numberOfDoses + (int) Math.ceil(Math.random() * (9 - numberOfDoses));
            var name = new JFixture().create(CertificatePersonNameDto.class);
            ReflectionTestUtils.setField(name, "familyName", LocalDate.now().minusYears(5).toString());
            ReflectionTestUtils.setField(name, "givenName", LocalDate.now().minusYears(5).toString());
            var personData = new JFixture().create(CertificatePersonDto.class);
            ReflectionTestUtils.setField(personData, "dateOfBirth", LocalDate.now().minusYears(5).toString());
            ReflectionTestUtils.setField(personData, "name", name);

            return vaccinationCertificateHcertDecodedDto;
        });
    }
}
