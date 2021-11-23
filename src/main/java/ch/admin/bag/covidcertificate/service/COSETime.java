package ch.admin.bag.covidcertificate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class COSETime {

    private final Clock clock;

    public Instant getIssuedAt() {
        return getInstant(LocalDateTime.now(clock));
    }

    public Instant calculateExpirationInstantPlusMonths(Integer months) {
        return getInstant(LocalDateTime.now(clock).plusMonths(months));
    }

    public Instant calculateExpirationInstantPlusDays(long days) {
        return getInstant(LocalDateTime.now(clock).plusDays(days));
    }

    private Instant getInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(clock.getZone().getRules().getOffset(localDateTime));
    }
}
