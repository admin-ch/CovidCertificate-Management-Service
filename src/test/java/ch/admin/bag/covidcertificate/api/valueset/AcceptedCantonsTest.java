package ch.admin.bag.covidcertificate.api.valueset;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcceptedCantonsTest {

    List<String> acceptedCantons =
            Arrays.asList("AG", "AI", "AR", "BE", "BL", "BS", "FR", "GE", "GL", "GR", "JU", "LU", "NE", "NW", "OW", "SG", "SH", "SO", "SZ", "TG", "TI", "UR", "VD", "VS", "ZG", "ZH");

    @Test
    void returnsTrue__ifAcceptedCantonUppercase() {
        acceptedCantons.forEach(canton -> assertTrue(AllowedSenders.isAccepted(canton.toUpperCase())));
    }

    @Test
    void returnsTrue__ifAcceptedCantonLowercase() {
        acceptedCantons.forEach(canton -> assertTrue(AllowedSenders.isAccepted(canton.toLowerCase())));
    }

    @Test
    void returnsFalse__ifNotCanton() {
        assertFalse(AllowedSenders.isAccepted("test"));
    }
}