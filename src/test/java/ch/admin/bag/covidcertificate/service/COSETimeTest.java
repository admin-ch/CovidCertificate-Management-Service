package ch.admin.bag.covidcertificate.service;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class COSETimeTest {
    private final JFixture jFixture = new JFixture();
    private Instant instant;
    private COSETime coseTime;

    @BeforeEach
    void init() {
        // given
        instant = jFixture.create(Instant.class);
        coseTime = new COSETime(Clock.fixed(instant, SWISS_TIMEZONE));
    }

    @Test
    void whenGetIssuedAt_thenOk() {
        // when
        Instant result = coseTime.getIssuedAt();
        // then
        assertEquals(instant, result);
    }

    @Test
    void whenGetExpiration_thenOk() {
        // when
        Instant result = coseTime.calculateExpirationInstantPlusMonths(24);
        // then
        long diff = ChronoUnit.MONTHS.between(getLocalDateTime(instant), getLocalDateTime(result));
        assertEquals(24, diff);
    }

    @Test
    @DisplayName("Given the calculateExpirationInstantPlusDays, when called, it should return an Instant ending now + 'days' at the end of the day '23:59:59'")
    void calculateExpirationInstantPlusDays() {
        Instant result = coseTime.calculateExpirationInstantPlusDays(30);
        LocalDateTime resultLocalDateTime = LocalDateTime.ofInstant(result, SWISS_TIMEZONE);
        assertEquals(LocalDateTime.now().plusDays(30).toLocalDate(), resultLocalDateTime.toLocalDate());
        assertEquals(23, resultLocalDateTime.getHour());
        assertEquals(59, resultLocalDateTime.getMinute());
        assertEquals(59, resultLocalDateTime.getSecond());
    }

    private LocalDateTime getLocalDateTime(Instant instant) {
        return instant.atZone(SWISS_TIMEZONE).toLocalDateTime();
    }
}
