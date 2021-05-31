package ch.admin.bag.covidcertificate.util;

import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

import static ch.admin.bag.covidcertificate.api.Constants.DAYS_UNTIL_RECOVERY_VALID;
import static ch.admin.bag.covidcertificate.api.Constants.RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateHelperTest {

    @Test
    public void testCalculateValidFrom() {
        LocalDate date = LocalDate.of(2021, Month.FEBRUARY, 13);

        LocalDate validFrom = DateHelper.calculateValidFrom(date);

        assertEquals(date.plusDays(DAYS_UNTIL_RECOVERY_VALID), validFrom);
    }

    @Test
    public void testCalculateValidUntil() {
        LocalDate date = LocalDate.of(2021, Month.FEBRUARY, 13);

        LocalDate validUntil = DateHelper.calculateValidUntil(date);

        assertEquals(date.plusDays(RECOVERY_CERTIFICATE_VALIDITY_IN_DAYS), validUntil);
    }
}
