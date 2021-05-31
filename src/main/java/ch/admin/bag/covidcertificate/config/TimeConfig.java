package ch.admin.bag.covidcertificate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    private static final String EUROPE_ZURICH = "Europe/Zurich";

    @Bean
    public Clock getClockInSwissZone() {
        return Clock.system(ZoneId.of(EUROPE_ZURICH));
    }
}
