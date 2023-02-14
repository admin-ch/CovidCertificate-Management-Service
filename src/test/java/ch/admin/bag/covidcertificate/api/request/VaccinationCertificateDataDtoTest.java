package ch.admin.bag.covidcertificate.api.request;

import ch.admin.bag.covidcertificate.TestModelProvider;
import ch.admin.bag.covidcertificate.api.request.CovidCertificatePersonDto;
import ch.admin.bag.covidcertificate.api.request.SystemSource;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateCreateDto;
import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.*;
import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.platform.commons.util.ReflectionUtils;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.admin.bag.covidcertificate.api.Constants.MAX_NB_OF_DOSES;
import static ch.admin.bag.covidcertificate.api.Constants.MIN_NB_OF_DOSES;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class VaccinationCertificateDataDtoTest {

    private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private static Stream<Arguments> provideTestParameters() {
        return IntStream.rangeClosed(MIN_NB_OF_DOSES, MAX_NB_OF_DOSES).mapToObj(Arguments::of);
    }

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeEach
    public void createValidator() {
        if (validatorFactory == null) {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        }
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
        validatorFactory = null;
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("Given 'medicinalProductCode' is blank, when validated, it should return an INVALID_MEDICINAL_PRODUCT error.")
    void medicinalProductCodeTest1(String medicinalProductCode) {
        VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                medicinalProductCode,
                2,
                2,
                YESTERDAY,
                "CH"
        );
        var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isMedicinalProductCodeValid").orElseThrow();

        Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isMedicinalProductCodeValid());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid medicinal product")));
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
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    medicinalProductCode,
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isMedicinalProductCodeValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isMedicinalProductCodeValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid medicinal product")));
        }

        @Test
        @DisplayName("Given 'medicinalProductCode' is 'blablabla', when validated, it should not return errors.")
        public void medicinalProductCodeTest2() {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "blablabla",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isMedicinalProductCodeValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isMedicinalProductCodeValid());
            assertTrue(violations.isEmpty());

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
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    2,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid number of doses")));

        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Given 'totalNumberOfDoses' is null, when validated, it should return an INVALID_DOSES error.")
        void dosesTest2(Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid number of doses")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'numberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest3(Integer numberOfDoses) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    9,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid number of doses")));
        }

        @ParameterizedTest
        @ValueSource(ints = {MIN_NB_OF_DOSES - 1, MAX_NB_OF_DOSES + 1})
        @DisplayName("Given 'totalNumberOfDoses' is outside the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should return an INVALID_DOSES error.")
        void dosesTest4(Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    9,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid number of doses")));
        }

        @ParameterizedTest
        @CsvSource({"3,2", "5,3", "9,2", "9,0"})
        @DisplayName("Given 'numberOfDoses' > 'totalNumberOfDoses' and 'totalNumberOfDoses' is not equal 1, when validated, it should return an INVALID_DOSES error.")
        void dosesTest5(Integer numberOfDoses, Integer totalNumberOfDoses) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    totalNumberOfDoses,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid number of doses")));
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' and 'totalNumberOfDoses' are equal and contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "], when validated, it should not return errors.")
        void dosesTest6(Integer dose) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    dose,
                    dose,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.isEmpty());
        }

        @ParameterizedTest
        @MethodSource("ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDtoTest#provideTestParameters")
        @DisplayName("Given 'numberOfDoses' is contained in the valid range [" + MIN_NB_OF_DOSES + ", " + MAX_NB_OF_DOSES + "] and 'totalNumberOfDoses' equal 1, when validated, it should not return errors.")
        void dosesTest7(Integer numberOfDoses) {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    numberOfDoses,
                    1,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isDosesValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isDosesValid());
            assertTrue(violations.isEmpty());
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
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    countryOfVaccination
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isCountryOfVaccinationValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isCountryOfVaccinationValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid country of vaccination")));
        }

        @Test
        @DisplayName("Given 'countryOfVaccination' is 'CH', when validated, it should not return errors.")
        public void countryOfVaccinationTest2() {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isCountryOfVaccinationValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isCountryOfVaccinationValid());
            assertTrue(violations.isEmpty());
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
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    vaccinationDate,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isVaccinationDateValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isVaccinationDateValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid vaccination date! Date cannot be in the future")));
        }

        @Test
        @DisplayName("Given 'vaccinationDate' is yesterday, when validated, it should not return errors.")
        public void vaccinationDateTest2() {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    YESTERDAY,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isVaccinationDateValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isVaccinationDateValid());
            assertTrue(violations.isEmpty());

        }

        @Test
        @DisplayName("Given 'vaccinationDate' is tomorrow, when validated, it should return an INVALID_VACCINATION_DATE error.")
        public void vaccinationDateTest3() {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    TOMORROW,
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isVaccinationDateValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isVaccinationDateValid());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid vaccination date! Date cannot be in the future")));

        }

        @Test
        @DisplayName("Given 'vaccinationDate' is now, when validated, it should not return errors.")
        public void vaccinationDateTest4() {
            VaccinationCertificateDataDto testee = new VaccinationCertificateDataDto(
                    "EU/1/20/1507",
                    2,
                    2,
                    LocalDate.now(),
                    "CH"
            );
            var methodToTest = ReflectionUtils.findMethod(testee.getClass(), "isVaccinationDateValid").orElseThrow();

            Set<ConstraintViolation<VaccinationCertificateDataDto>> violations = validator.forExecutables().validateReturnValue(testee, methodToTest, testee.isVaccinationDateValid());
            assertTrue(violations.isEmpty());

        }
    }
}