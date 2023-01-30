package ch.admin.bag.covidcertificate.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LuhnChecksumTest {

    @Test
    void validLuhnChecksum() {
        assertTrue(LuhnChecksum.validateCheckCharacter("U4RZ8RHTB"));
    }

    @Test
    void invalidLuhnChecksum() {
        assertFalse(LuhnChecksum.validateCheckCharacter("U4RZ8RHTX"));
    }
}