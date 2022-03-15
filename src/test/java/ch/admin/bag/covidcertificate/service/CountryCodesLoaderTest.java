package ch.admin.bag.covidcertificate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {CountryCodesLoader.class, ObjectMapper.class})
class CountryCodesLoaderTest {
    @Autowired
    private CountryCodesLoader countryCodesLoader;

    @Nested
    class CountryCodeValueSetInitializationTest {
        @Test
        void allCountryCodesDeAreLoaded() {
            var countryCodes = countryCodesLoader.getCountryCodes();
            assertEquals(251, countryCodes.getDe().size());
        }

        @Test
        void countryCodeDeValueSetsAreCorrectlyInitialized() {
            var countryCodes = countryCodesLoader.getCountryCodes();

            var result = countryCodes.getDe().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("de", result.getLang());
            assertTrue(result.isActive());
        }

        @Test
        void allCountryCodesEnAreLoaded() {
            var countryCodes = countryCodesLoader.getCountryCodes();
            assertEquals(251, countryCodes.getEn().size());
        }

        @Test
        void countryCodeEnValueSetsAreCorrectlyInitialized() {
            var countryCodes = countryCodesLoader.getCountryCodes();

            var result = countryCodes.getEn().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("en", result.getLang());
            assertTrue(result.isActive());
        }

        @Test
        void allCountryCodesFrAreLoaded() {
            var countryCodes = countryCodesLoader.getCountryCodes();
            assertEquals(251, countryCodes.getFr().size());
        }

        @Test
        void countryCodeFrValueSetsAreCorrectlyInitialized() {
            var countryCodes = countryCodesLoader.getCountryCodes();

            var result = countryCodes.getFr().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("fr", result.getLang());
            assertTrue(result.isActive());
        }


        @Test
        void allCountryCodesItAreLoaded() {
            var countryCodes = countryCodesLoader.getCountryCodes();
            assertEquals(251, countryCodes.getIt().size());
        }

        @Test
        void countryCodeItValueSetsAreCorrectlyInitialized() {
            var countryCodes = countryCodesLoader.getCountryCodes();

            var result = countryCodes.getIt().stream().filter(it -> it.getShortName().equals("TE")).findFirst().orElse(null);
            assertNotNull(result);
            assertEquals("TE", result.getShortName());
            assertEquals("Test", result.getDisplay());
            assertEquals("it", result.getLang());
            assertTrue(result.isActive());
        }
    }
}