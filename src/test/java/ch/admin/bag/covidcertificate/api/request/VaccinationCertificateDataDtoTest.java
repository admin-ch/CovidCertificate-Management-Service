package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.INVALID_COUNTRY_OF_VACCINATION;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_VACCINATION_DATE;
import static ch.admin.bag.covidcertificate.api.Constants.MAX_NB_OF_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.MIN_NB_OF_DOSES;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("VaccinationCertificateDataDtoTest")
@DisplayName("Tests for the VaccinationCertificateDataDto")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VaccinationCertificateDataDtoTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private static Stream<Arguments> provideTestParameters() {
        return IntStream.rangeClosed(MIN_NB_OF_DOSES, MAX_NB_OF_DOSES).mapToObj(Arguments::of);
    }

    private void assertError(CreateCertificateError expectedCreateCertificateError, VaccinationCertificateDataDto vaccinationCertificateDataDto) {
        CreateCertificateException actualException = assertThrows(CreateCertificateException.class, vaccinationCertificateDataDto::validate);
        assertEquals(expectedCreateCertificateError.getHttpStatus(), actualException.getError().getHttpStatus());
        assertEquals(expectedCreateCertificateError.getErrorCode(), actualException.getError().getErrorCode());
        assertEquals(expectedCreateCertificateError.getErrorMessage(), actualException.getError().getErrorMessage());
        assertEquals(expectedCreateCertificateError, actualException.getError());
    }

    private void assertNoError(VaccinationCertificateDataDto vaccinationCertificateDataDto) {
        assertDoesNotThrow(vaccinationCertificateDataDto::validate);
    }

    @Nested
    @Tag("VaccinationCertificateDataDto.medicinalProductCode")
    @DisplayName("medicinalProductCode can't be blank.")
    class MedicinalProductCodeTests {

        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Given 'medicinalProductCode' is blank, when validated, it should return an INVALID_MEDICINAL_PRODUCT error.")
        void medicinalProductCodeTest1(String medicinalProductCode) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    medicinalProductCode,
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_MEDICINAL_PRODUCT, vaccinationCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'medicinalProductCode' is 'blablabla', when validated, it should not return errors.")
        public void medicinalProductCodeTest2() {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "blablabla",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }
    }

    @Nested
    @Tag("VaccinationCertificateDataDto.doses")
    @DisplayName("'numberOfDoses' and 'totalNumberOfDoses' must be valid.")
    class DosesTests {

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'numberOfDoses' is null, when validated, it should return an INVALID_DOSES error.")
        void dosesTest1(Integer numberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'totalNumberOfDoses' is null, when validated, it should return an INVALID_DOSES error.")
        void dosesTest2(Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'numberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest3(Integer numberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    9,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'totalNumberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest4(Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    9,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @CsvSource({"3,2", "5,3", "9,2", "9,0"})
        @DisplayName("Given 'numberOfDoses' > 'totalNumberOfDoses' and 'totalNumberOfDoses' is not equal 1, when validated, it should return an INVALID_DOSES error.")
        void dosesTest5(Integer numberOfDoses, Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' and 'totalNumberOfDoses' are equal and contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should not return errors.")
        void dosesTest6(Integer dose) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    dose,
                    dose,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' is contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "] and 'totalNumberOfDoses' equal 1, when validated, it should not return errors.")
        void dosesTest7(Integer numberOfDoses) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    1,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }
    }

    @Nested
    @Tag("VaccinationCertificateDataDto.countryOfVaccination")
    @DisplayName("countryOfVaccination can't be blank.")
    class CountryOfVaccinationTests {

        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Given 'countryOfVaccination' is blank, when validated, it should return an INVALID_DOSES error.")
        void countryOfVaccinationTest1(String countryOfVaccination) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    countryOfVaccination
            );
            assertError(INVALID_COUNTRY_OF_VACCINATION, vaccinationCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'countryOfVaccination' is 'CH', when validated, it should not return errors.")
        public void countryOfVaccinationTest2() {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }
    }


    @Nested
    @Tag("VaccinationCertificateDataDto.vaccinationDate")
    @DisplayName("vaccinationDate can't be null or in the future.")
    class VaccinationDateTests {

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'vaccinationDate' is null, when validated, it should return an INVALID_VACCINATION_DATE error.")
        void vaccinationDateTest1(LocalDate vaccinationDate) {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    vaccinationDate,
                    "CH"
            );
            assertError(INVALID_VACCINATION_DATE, vaccinationCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is yesterday, when validated, it should not return errors.")
        public void vaccinationDateTest2() {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is tomorrow, when validated, it should return an INVALID_VACCINATION_DATE error.")
        public void vaccinationDateTest3() {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    TOMORROW,
                    "CH"
            );
            assertError(INVALID_VACCINATION_DATE, vaccinationCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is now, when validated, it should not return errors.")
        public void vaccinationDateTest4() {
            VaccinationCertificateDataDto vaccinationCertificateDataDto = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    LocalDate.now(),
                    "CH"
            );
            assertNoError(vaccinationCertificateDataDto);
        }
    }
}