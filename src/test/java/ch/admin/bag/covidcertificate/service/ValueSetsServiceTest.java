package ch.admin.bag.covidcertificate.service;

import ch.admin.bag.covidcertificate.api.exception.CreateCertificateException;
import ch.admin.bag.covidcertificate.api.request.TestCertificateDataDto;
import ch.admin.bag.covidcertificate.api.valueset.CountryCode;
import ch.admin.bag.covidcertificate.api.valueset.TestValueSet;
import ch.admin.bag.covidcertificate.api.valueset.ValueSetsDto;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
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

import static ch.admin.bag.covidcertificate.FixtureCustomization.*;
import static ch.admin.bag.covidcertificate.api.Constants.*;
import static ch.admin.bag.covidcertificate.api.valueset.AcceptedLanguages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValueSetsServiceTest {
    @InjectMocks
    private ValueSetsService service;

    @Mock
    private ValueSetsLoader valueSetsLoader;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void setUp() {
        customizeVaccinationValueSet(fixture);
        customizeTestValueSet(fixture);
        customizeCountryCode(fixture);
        lenient().when(valueSetsLoader.getValueSets()).thenReturn(fixture.create(ValueSetsDto.class));
    }

    @Nested
    class GetVaccinationValueSet{
        @Test
        void shouldReturnVaccinationValueSet_ifMedicinalProductCodeExists(){
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            var expected = valueSetsDto.getVaccinationSets().stream().findFirst().get();
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getVaccinationValueSet(expected.getMedicinalProductCode());

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifMedicinalProductCodeNotExists(){
            var medicinalProductCode = fixture.create(String.class);
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getVaccinationValueSet(medicinalProductCode)
            );

            assertEquals(INVALID_MEDICINAL_PRODUCT, actual.getError());
        }
    }

    @Nested
    class GetAllTestValueSet{
        @ParameterizedTest
        @CsvSource(value = {":","'':''", "' ':' '",  "'\t':'\t'", "'\n':'\n'"}, delimiter = ':')
        void shouldThrowCreateCertificateException_ifTestTypeCodeAndManufacturerCodeAreNullOrBlank(String typeCode, String manufacturerCode){
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getAllTestValueSet(typeCode, manufacturerCode)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheTestTypeCode_ifTypeCodeIsPCR_andManufacturerIsNullOrBlank(String manufacturerCode){
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "typeCode", PCR_TYPE_CODE);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getAllTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getAllTestValueSet(PCR_TYPE_CODE, manufacturerCode);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsPCR_andManufacturerIsNotBlank(){
            var manufacturer = fixture.create(String.class);
            var actual= assertThrows(CreateCertificateException.class,
                    () -> service.getAllTestValueSet(PCR_TYPE_CODE, manufacturer)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @Test
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNotPCR_andManufacturerIsNotEmpty(){
            var manufacturer = fixture.create(String.class);
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "manufacturerCodeEu", manufacturer);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getAllTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getAllTestValueSet(NONE_PCR_TYPE_CODE, manufacturer);

            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNullOrBlank_andManufacturerIsNotEmpty(String typeCode){
            var manufacturer = fixture.create(String.class);
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "manufacturerCodeEu", manufacturer);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getAllTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getAllTestValueSet(typeCode, manufacturer);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsNotPCR_andManufacturerIsEmpty(){
            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getAllTestValueSet(NONE_PCR_TYPE_CODE, null)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }
    }

    @Nested
    class GetChAcceptedTestValueSet{
        @ParameterizedTest
        @CsvSource(value = {":","'':''", "' ':' '",  "'\t':'\t'", "'\n':'\n'"}, delimiter = ':')
        void shouldThrowCreateCertificateException_ifTestTypeCodeAndManufacturerCodeAreNullOrBlank(String typeCode, String manufacturerCode){
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", typeCode);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", manufacturerCode);

            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getChAcceptedTestValueSet(testCertificateDataDto)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheTestTypeCode_ifTypeCodeIsPCR_andManufacturerIsNullOrBlank(String manufacturerCode){
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "typeCode", PCR_TYPE_CODE);
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", PCR_TYPE_CODE);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", manufacturerCode);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getChAcceptedTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getChAcceptedTestValueSet(testCertificateDataDto);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsPCR_andManufacturerIsNotBlank(){
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", PCR_TYPE_CODE);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", fixture.create(String.class));

            var actual= assertThrows(CreateCertificateException.class,
                    () -> service.getChAcceptedTestValueSet(testCertificateDataDto)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }

        @Test
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNotPCR_andManufacturerIsNotEmpty(){
            var manufacturer = fixture.create(String.class);
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "manufacturerCodeEu", manufacturer);
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", NONE_PCR_TYPE_CODE);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", manufacturer);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getChAcceptedTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getChAcceptedTestValueSet(testCertificateDataDto);

            assertEquals(expected, actual);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnTestValueSetUsingTheManufacturerCode_ifTypeCodeIsNullOrBlank_andManufacturerIsNotEmpty(String typeCode){
            var manufacturer = fixture.create(String.class);
            var expected = fixture.create(TestValueSet.class);
            ReflectionTestUtils.setField(expected, "manufacturerCodeEu", manufacturer);
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", typeCode);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", manufacturer);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getChAcceptedTestValueSets().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual= service.getChAcceptedTestValueSet(testCertificateDataDto);

            assertEquals(expected, actual);
        }

        @Test
        void shouldThrowCreateCertificateException_ifTypeCodeIsNotPCR_andManufacturerIsEmpty(){
            var testCertificateDataDto = fixture.create(TestCertificateDataDto.class);
            ReflectionTestUtils.setField(testCertificateDataDto, "typeCode", NONE_PCR_TYPE_CODE);
            ReflectionTestUtils.setField(testCertificateDataDto, "manufacturerCode", null);

            var actual = assertThrows(CreateCertificateException.class,
                    () -> service.getChAcceptedTestValueSet(testCertificateDataDto)
            );

            assertEquals(INVALID_TYP_OF_TEST, actual.getError());
        }
    }

    @Nested
    class GetCountryCode{
        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsDE(){
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getCountryCodes().getDe().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getCountryCode(countryShort, DE);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsIT(){
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getCountryCodes().getIt().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getCountryCode(countryShort, IT);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsFR(){
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getCountryCodes().getFr().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getCountryCode(countryShort, FR);

            assertEquals(expected, actual);
        }

        @Test
        void shouldReturnCorrectCountryCode_ifLanguageIsRM(){
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getCountryCodes().getRm().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getCountryCode(countryShort, RM);

            assertEquals(expected, actual);
        }
    }

    @Nested
    class GetCountryCodeEn{
        @Test
        void shouldReturnCorrectCountryCode(){
            var countryShort = fixture.create(String.class);
            var expected = fixture.create(CountryCode.class);
            ReflectionTestUtils.setField(expected, "shortName", countryShort);
            var valueSetsDto = fixture.create(ValueSetsDto.class);
            valueSetsDto.getCountryCodes().getEn().add(expected);
            when(valueSetsLoader.getValueSets()).thenReturn(valueSetsDto);

            var actual = service.getCountryCodeEn(countryShort);

            assertEquals(expected, actual);
        }
    }
}
