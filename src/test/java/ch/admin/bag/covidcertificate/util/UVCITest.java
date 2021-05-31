package ch.admin.bag.covidcertificate.util;

import ch.admin.bag.covidcertificate.api.request.VaccinationCertificateDataDto;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UVCITest {

    private final VaccinationCertificateDataDto vaccineDataDto = mock(VaccinationCertificateDataDto.class);

    @Test
    void testUppercase() {
        String uvci = UVCI.generateUVCI(vaccineDataDto.toString());
        String[] results = uvci.split(":");

        for (char c : results[results.length - 1].toCharArray()) {
            if (c != ':') {
                assertTrue(Character.isUpperCase(c) || Character.isDigit(c));
            }
        }
    }

    @Test
    void testLength() {
        String uvci = UVCI.generateUVCI(vaccineDataDto.toString());

        assertNotNull(uvci);
        assertEquals(39, uvci.length());
    }


    @Test
    void testSimilarityMultiple() {
        Set testSet = new HashSet();
        int numberOfRuns = 100000;
        for (int i = 0; i != numberOfRuns; i++) {
            String uvci = UVCI.generateUVCI(vaccineDataDto.toString());
            testSet.add(uvci);
        }
        assertEquals(numberOfRuns, testSet.size());
    }

    @Test
    void testSimilarity() {
        String uvciOne = UVCI.generateUVCI(vaccineDataDto.toString());
        String uvciTwo = UVCI.generateUVCI(vaccineDataDto.toString());

        assertNotEquals(uvciOne, uvciTwo);
    }

    @Test
    void testComponents() {
        String uvci = UVCI.generateUVCI(vaccineDataDto.toString());
        String[] results = uvci.split(":");

        assertEquals("urn", results[0]);
        assertEquals("uvci", results[1]);
        assertEquals("01", results[2]);
        assertEquals("CH", results[3]);
        assertEquals(24, results[4].length());
    }
}
