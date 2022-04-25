package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.exception.ValueSetException;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.CountryCodes;
import ch.admin.bag.covidcertificate.api.valueset.IssuableTestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestDto;
import ch.admin.bag.covidcertificate.api.valueset.TestType;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import ch.admin.bag.covidcertificate.domain.RapidTest;
import ch.admin.bag.covidcertificate.domain.RapidTestRepository;
import ch.admin.bag.covidcertificate.domain.Vaccine;
import ch.admin.bag.covidcertificate.domain.VaccineRepository;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeCountryCode;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeIssuableVaccineDto;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeRapidTest;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeTestValueSet;
import static ch.admin.bag.covidcertificate.FixtureCustomization.customizeVaccine;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_MEDICINAL_PRODUCT;
import static ch.admin.bag.covidcertificate.api.Constants.INVALID_TYP_OF_TEST;
import static ch.admin.bag.covidcertificate.api.Constants.UNSUPPORTED_LANGUAGE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.DE;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.FR;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.IT;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.RM;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValueSetsServiceTest {
    @InjectMocks
    private ValueSetsService service;

    @Mock
    private CountryCodesLoader countryCodesLoader;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private RapidTestRepository rapidTestRepository;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void setUp() {
        customizeIssuableVaccineDto(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);
        customizeVaccine(fixture);
        customizeRapidTest(fixture);
        lenient().when(countryCodesLoader.getCountryCodes()).thenReturn(fixture.create(CountryCodes.class));
        lenient().when(vaccineRepository.findAll()).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(vaccineRepository.findAllGatewayApiActive()).thenReturn(
                fixture.collections().createCollection(List.class, Vaccine.class));
        lenient().when(rapidTestRepository.findAll()).thenReturn(
                fixture.collections().createCollection(List.class, RapidTest.class));
        lenient().when(rapidTestRepository.findAllActiveAndChIssuable()).thenReturn(
                fixture.collections().createCollection(List.class, RapidTest.class));
    }

    @Nested
    class GetVaccinationValueSet {
        @Test
        void shouldReturnVaccinationValueSet_ifMedicinalProductCodeExists() {
            var vaccines = fixture.collections().createCollection(List.class, Vaccine.class);
            var vaccine = (Vaccine) vaccines.stream().findFirst().or(Assertions::fail).get();
            var expected = vaccine.getCode();

            lenient().when(vaccineRepository.findAll()).thenReturn(vaccines);

            var actual = service.getVaccinationValueSet(expected).getProductCode();

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifMedicinalProductCodeNotExists() {
            var medicinalProductCode = fixture.create(String.class);
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getVaccinationValueSet(medicinalProductCode)
            );

            assertEquals(INVALID_MEDICINAL_PRODUCT, actual.getError());
        }
    }

    @Nested
    @Disabled
    class GetAllTestValueSet {
        @ParameterizedTest
        @CsvSource(value = {":", "'':''", "' ':' '", "'\t':'\t'", "'\n':'\n'"}, delimiter = ':')
        void shouldThrowCreateCertificateException_ifTestTypeCodeAndManufacturerCodeAreNullOrBlank(String typeCode, String manufacturerCode) {
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.validateAndGetIssuableTestDto(typeCode, manufacturerCode)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @Disabled("see todo")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheTestTypeCode_ifTypeCodeIsPCR_andManufacturerIsNullOrBlank(String manufacturerCode) {
            var expected = fixture.create(IssuableTestDto.class);
            ReflectionTestUtils.setField(expected, "testType", TestType.PCR);
            var valueSetsDto = fixture.create(ValueSetsDto.class);

            // @Todo: Mock the DB call to include expected at getIssuableRapidTests(), once DB is connected

            var actual = service.validateAndGetIssuableTestDto(TestType.PCR.typeCode, manufacturerCode);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsPCR_andManufacturerIsNotBlank() {
            var manufacturer = fixture.create(String.class);
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.validateAndGetIssuableTestDto(TestType.PCR.typeCode, manufacturer)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @Disabled("see todo")
        @Test
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNotPCR_andManufacturerIsNotEmpty() {
            var manufacturer = fixture.create(String.class);
            var expected = fixture.create(IssuableTestDto.class);
            ReflectionTestUtils.setField(expected, "manufacturerCodeEu", manufacturer);
            var valueSetsDto = fixture.create(ValueSetsDto.class);

            // @Todo: Mock the DB call to include expected at getIssuableRapidTests(), once DB is connected

            var actual = service.validateAndGetIssuableTestDto(TestType.RAPID_TEST.typeCode, manufacturer);

            assertEquals(expected, actual);
        }

        @Disabled("see todo")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNullOrBlank_andManufacturerIsNotEmpty(String typeCode) {
            var testCode = fixture.create(String.class);
            var expected = fixture.create(TestDto.class);
            ReflectionTestUtils.setField(expected, "code", testCode);
            var valueSetsDto = fixture.create(ValueSetsDto.class);

            // @Todo: Mock the DB call to include expected at getIssuableRapidTests(), once DB is connected

            var actual = service.validateAndGetIssuableTestDto(typeCode, testCode);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsNotPCR_andManufacturerIsEmpty() {
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.validateAndGetIssuableTestDto(TestType.RAPID_TEST.typeCode, null)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }
    }

    @Nested
    class GetCountryCode {
        @Test
        void shouldReturnCorrectCountryCodes() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodes();

            assertDoesNotThrow(() -> ReflectionTestUtils.getField(actual, "de"));
            assertDoesNotThrow(() -> ReflectionTestUtils.getField(actual, "fr"));
            assertDoesNotThrow(() -> ReflectionTestUtils.getField(actual, "it"));
            assertDoesNotThrow(() -> ReflectionTestUtils.getField(actual, "rm"));
            assertDoesNotThrow(() -> ReflectionTestUtils.getField(actual, "en"));
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsDE() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("de");

            assertEquals(countryCodes.getDe(), actual);
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsFR() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("fr");

            assertEquals(countryCodes.getFr(), actual);
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsIT() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("it");

            assertEquals(countryCodes.getIt(), actual);
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsRM() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("rm");

            assertEquals(countryCodes.getRm(), actual);
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsEN() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("en");

            assertEquals(countryCodes.getEn(), actual);
        }

        @Test
        void shouldReturnCorrectCountryCodesByLanguage_ifLanguageIsUpperCase() {
            var countryCodes = fixture.create(CountryCodes.class);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodesForLanguage("dE");

            assertEquals(countryCodes.getDe(), actual);
        }

        @Test
        void shouldThrowValueSetException_ifLanguageIsNotSupported() {
            var actual = assertThrows(ValueSetException.class,
                    () -> service.getCountryCodesForLanguage("dexxxxx"));

            assertEquals(UNSUPPORTED_LANGUAGE, actual.getError());
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsDE() {
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var countryCodes = fixture.create(CountryCodes.class);
            countryCodes.getDe().add(expected);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCode(countryShort, DE);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsIT() {
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var countryCodes = fixture.create(CountryCodes.class);
            countryCodes.getIt().add(expected);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCode(countryShort, IT);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsFR() {
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var countryCodes = fixture.create(CountryCodes.class);
            countryCodes.getFr().add(expected);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCode(countryShort, FR);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsRM() {
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var countryCodes = fixture.create(CountryCodes.class);
            countryCodes.getRm().add(expected);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCode(countryShort, RM);

            assertEquals(expected, actual);
        }
    }

    @Nested
    class GetCountryCodeEn {
        @Test
        void shouldReturnCorrectCountryCode() {
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var countryCodes = fixture.create(CountryCodes.class);
            countryCodes.getEn().add(expected);
            when(countryCodesLoader.getCountryCodes()).thenReturn(countryCodes);

            var actual = service.getCountryCodeEn(countryShort);

            assertEquals(expected, actual);
        }
    }
}
