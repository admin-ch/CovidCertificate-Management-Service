package ch.admin.bag.covidcertificate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValueSetsLoader.class, ObjectMapper.class})
class ValueSetsLoaderTest {
    @Autowired
    private ValueSetsLoader valueSetsLoader;

    @Nested
    class CountryCodeValueSetInitializationTest {
        @Test
        void allCountryCodesDeAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(3, dto.getCountryCodes().getDe().size());
        }

        @Test
        void countryCodeDeValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getCountryCodes().getDe().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("de", result.getLang());
            assertTrue(result.isActive());
        }

        @Test
        void allCountryCodesEnAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(3, dto.getCountryCodes().getEn().size());
        }

        @Test
        void countryCodeEnValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getCountryCodes().getEn().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("en", result.getLang());
            assertTrue(result.isActive());
        }

        @Test
        void allCountryCodesFrAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(3, dto.getCountryCodes().getFr().size());
        }

        @Test
        void countryCodeFrValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getCountryCodes().getFr().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("fr", result.getLang());
            assertTrue(result.isActive());
        }


        @Test
        void allCountryCodesItAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(3, dto.getCountryCodes().getIt().size());
        }

        @Test
        void countryCodeItValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getCountryCodes().getIt().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("it", result.getLang());
            assertTrue(result.isActive());
        }
    }

    @Nested
    class VaccinationValueSetInitializationTest {
        @Test
        void allVaccinationValueSetsAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(5, dto.getVaccinationSets().size());
        }

        @Test
        void vaccinationValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getVaccinationSets().stream().filter(it -> it.getMedicinalProductCode().equals("testCode")).findFirst().orElse(null);

            assertNotNull(result);
            assertEquals("testName", result.getMedicinalProduct());
            assertEquals("testCode", result.getMedicinalProductCode());
            assertEquals("testProphylaxis", result.getProphylaxis());
            assertEquals("testProphylaxisCode", result.getProphylaxisCode());
            assertEquals("testAuthHolder", result.getAuthHolder());
            assertEquals("testAuthCode", result.getAuthHolderCode());
            assertTrue(result.isActive());
        }
    }

    @Nested
    class TestValueSetInitializationTest {
        @Test
        void allTestValueSetsAreLoaded() {
            var dto = valueSetsLoader.getValueSets();
            assertEquals(8, dto.getChAcceptedTestValueSets().size());
        }

        @Test
        void testValueSetsAreCorrectlyInitialized() {
            var dto = valueSetsLoader.getValueSets();

            var result = dto.getChAcceptedTestValueSets().stream().filter(it -> it.getName().equals("testName")).findFirst().orElse(null);

            assertNotNull(result);
            assertEquals("testName", result.getName());
            assertEquals("testType", result.getType());
            assertEquals("testTypeCode", result.getTypeCode());
            assertEquals("testManufacturer", result.getManufacturer());
            assertEquals("testSwissTestKit", result.getSwissTestKit());
            assertEquals("testManufacturerCodeEu", result.getManufacturerCodeEu());
            assertTrue(result.isEuAccepted());
            assertTrue(result.isChAccepted());
            assertTrue(result.isActive());
        }
    }
}