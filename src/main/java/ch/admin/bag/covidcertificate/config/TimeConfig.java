package ch.admin.bag.covidcertificate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

import static ch.admin.bag.covidcertificate.api.Constants.SWISS_TIMEZONE;

@Configuration
public class TimeConfig {

    @Bean
    public Clock getClockInSwissZone() {
        return Clock.system(SWISS_TIMEZONE);
    }
}
