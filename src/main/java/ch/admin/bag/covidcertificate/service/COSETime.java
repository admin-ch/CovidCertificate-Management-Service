package ch.admin.bag.covidcertificate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class COSETime {
    private static final Integer EXPIRATION_PERIOD = 24;

    private final Clock clock;

    public Instant getIssuedAt() {
        return getInstant(LocalDateTime.now(clock));
    }

    public Instant getExpiration() {
        return getInstant(LocalDateTime.now(clock).plusMonths(EXPIRATION_PERIOD));
    }

    private Instant getInstant(LocalDateTime localDateTime) {
        return localDateTime.toInstant(clock.getZone().getRules().getOffset(localDateTime));
    }
}
