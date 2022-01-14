package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateError;
import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
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

@Tag("VaccinationTouristCertificateDataDtoTest")
@DisplayName("Tests for the VaccinationTouristCertificateDataDto")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VaccinationTouristCertificateDataDtoTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private static Stream<Arguments> provideTestParameters() {
        return IntStream.rangeClosed(MIN_NB_OF_DOSES, MAX_NB_OF_DOSES).mapToObj(Arguments::of);
    }

    private void assertError(CreateCertificateError expectedCreateCertificateError, VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto) {
        CreateCertificateException actualException = assertThrows(CreateCertificateException.class, VaccinationTouristCertificateDataDto::validate);
        assertEquals(expectedCreateCertificateError.getHttpStatus(), actualException.getError().getHttpStatus());
        assertEquals(expectedCreateCertificateError.getErrorCode(), actualException.getError().getErrorCode());
        assertEquals(expectedCreateCertificateError.getErrorMessage(), actualException.getError().getErrorMessage());
        assertEquals(expectedCreateCertificateError, actualException.getError());
    }

    private void assertNoError(VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto) {
        assertDoesNotThrow(VaccinationTouristCertificateDataDto::validate);
    }

    @Nested
    @Tag("VaccinationTouristCertificateDataDto.medicinalProductCode")
    @DisplayName("medicinalProductCode can't be blank.")
    class MedicinalProductCodeTests {

        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Given 'medicinalProductCode' is blank, when validated, it should return an INVALID_MEDICINAL_PRODUCT error.")
        void medicinalProductCodeTest1(String medicinalProductCode) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    medicinalProductCode,
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_MEDICINAL_PRODUCT, VaccinationTouristCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'medicinalProductCode' is 'blablabla', when validated, it should not return errors.")
        public void medicinalProductCodeTest2() {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "blablabla",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }
    }

    @Nested
    @Tag("VaccinationTouristCertificateDataDto.doses")
    @DisplayName("'numberOfDoses' and 'totalNumberOfDoses' must be valid.")
    class DosesTests {

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'numberOfDoses' is null, when validated, it should return an INVALID_DOSES error.")
        void dosesTest1(Integer numberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'totalNumberOfDoses' is null, when validated, it should return an INVALID_DOSES error.")
        void dosesTest2(Integer totalNumberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'numberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest3(Integer numberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    9,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'totalNumberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest4(Integer totalNumberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    9,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @CsvSource({"3,2", "5,3", "9,2", "9,0"})
        @DisplayName("Given 'numberOfDoses' > 'totalNumberOfDoses' and 'totalNumberOfDoses' is not equal 1, when validated, it should return an INVALID_DOSES error.")
        void dosesTest5(Integer numberOfDoses, Integer totalNumberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            assertError(INVALID_DOSES, VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' and 'totalNumberOfDoses' are equal and contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should not return errors.")
        void dosesTest6(Integer dose) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    dose,
                    dose,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationTouristCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' is contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "] and 'totalNumberOfDoses' equal 1, when validated, it should not return errors.")
        void dosesTest7(Integer numberOfDoses) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    1,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }
    }

    @Nested
    @Tag("VaccinationTouristCertificateDataDto.countryOfVaccination")
    @DisplayName("countryOfVaccination can't be blank.")
    class CountryOfVaccinationTests {

        @ParameterizedTest
        @NullSource
        @EmptySource
        @DisplayName("Given 'countryOfVaccination' is blank, when validated, it should return an INVALID_DOSES error.")
        void countryOfVaccinationTest1(String countryOfVaccination) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    countryOfVaccination
            );
            assertError(INVALID_COUNTRY_OF_VACCINATION, VaccinationTouristCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'countryOfVaccination' is 'CH', when validated, it should not return errors.")
        public void countryOfVaccinationTest2() {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }
    }


    @Nested
    @Tag("VaccinationTouristCertificateDataDto.vaccinationDate")
    @DisplayName("vaccinationDate can't be null or in the future.")
    class VaccinationDateTests {

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'vaccinationDate' is null, when validated, it should return an INVALID_VACCINATION_DATE error.")
        void vaccinationDateTest1(LocalDate vaccinationDate) {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    vaccinationDate,
                    "CH"
            );
            assertError(INVALID_VACCINATION_DATE, VaccinationTouristCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is yesterday, when validated, it should not return errors.")
        public void vaccinationDateTest2() {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is tomorrow, when validated, it should return an INVALID_VACCINATION_DATE error.")
        public void vaccinationDateTest3() {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    TOMORROW,
                    "CH"
            );
            assertError(INVALID_VACCINATION_DATE, VaccinationTouristCertificateDataDto);
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is now, when validated, it should not return errors.")
        public void vaccinationDateTest4() {
            VaccinationTouristCertificateDataDto VaccinationTouristCertificateDataDto = new VaccinationTouristCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    LocalDate.now(),
                    "CH"
            );
            assertNoError(VaccinationTouristCertificateDataDto);
        }
    }
}