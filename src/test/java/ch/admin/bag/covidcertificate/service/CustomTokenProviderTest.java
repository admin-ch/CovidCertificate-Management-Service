package ch.admin.bag.covidcertificate.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
class CustomTokenProviderTest {

    @Test
    void createToken() throws NoSuchAlgorithmException {

        CustomTokenProvider tokenProvider = new CustomTokenProvider();

        ReflectionTestUtils.setField(tokenProvider, "issuer", "http://localhost:8113");
        ReflectionTestUtils.setField(tokenProvider, "tokenValidity", 43200000);
        ReflectionTestUtils.setField(tokenProvider, "privateKey", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbuDmx93wy1N9SHb2GVbqr6lkJk9jwxwzQlsXVnBdnzRnHaA0MCJRNCtjVy4f0qmAHQk4hMzJHrL57s3hAWVqId6PBQs974JQk6WCJN3/CpPrgkNZeifw6OpmqlcTd6zu5u0MbUs6Mh42j1RlyrO/NFyqL5Eg9hD5YcHt97GfV+nsVJvRgS4wMcu6ouaIUrDt6WZ/o7CC4v0nZeEQleX2gtgMqOSQfWWagu1ZwNQ5Hg4QNP5IysMZC7xzszvdl7W/LMPfAuuZUOg0AJMsAwmZThvxk/9o41SnJl6ed4qlZ4uOEZfBeZ5e0iEkwrAFwSnsyQH0IW3Wr/UskBrAg/x9rAgMBAAECggEAVyw6oDY7gPlKS136y0kSx0rZrVLnD2Ne+SZuebZ4I9PdqpPFOgdTfg2kdYsLARyfxXCI7G0MqLM7r2Q43U0oMV1Iftg37tE6Ha/IKwi2rPBOwYhTeXklijNj8usE2nblaIQ8fP9OQb1gvWZ+aIQHeniNiOKyzj1J6ZiOiV/egRpoT7+3sY6csX6uSO5/0r3rL7TsMgmn/mH4NwHm5UItrGmmKO4LR8cLiOmyfCbB+4/UjXj9JAmZDe7Nn+/W4H4wWWNk8MC79ke/3M5i9EG6hNF3AbRf5R2sMiMW59jN7AeRXGoiCOfrGXWNvE78+Pom2qhbdFFx2djtVK4YbSLVgQKBgQDMqKjlQLqdZ5fo2M49sGVSP1YuUlWbxj4BeJku/ZCO5DzZ4fU3v5VjWztFbhTdPVghbo1tGqEGSFZ/LAO7wWUGu0XKs/r01QACxSNcThB4X3/RjF2rwV+lLgCHoVctIP3roA+tOoszzwNxTqqXd08T8ckiW4+nf8Ft5EtFVvvLJQKBgQDCyKeJ7EcJNusZ2uIQic4gZjgOguXUDACC0Tn5wMyN81niCQugFJzqCkrYJABGPGWNEEFPbYiuSVyxvwZ37Z/Zi+3d+hDL74PLOz24z7CZK253oqFG9k3Ddvnd7bK+ZLt0dYF6t7hNHI4PPs3+Li/D/poIapzfLPCte2HJfyIDTwKBgGtVbTbGqtiQkxAQXKHn2Eu5YfZrQfCvmKdm21fUrjLyqqNOqS+yr6NrHnu8Tv71BDqMY2m8FIVZ/Ns3d0HKHLTaFLFJkS1EZHwPbgsj+elXlI6OwjWo9gOIS8jWKgVGD0W7LV2ZnZXvVQvgyQElFnkMToNRZ9bd3tFGcN+NzgJtAoGBAMETKI8ceCV4HH6aaq8+CeYvrK0lry8LXo5NWoxoQdsLNzNJCA77n7aV0S6CMQtt3rN/Q126E1u/OHSwB3dlQafgfj4kG/YqSpdu93Vz2Xdah7tqpzax+s8f5fnIHf9/1hhQSbIc3kEBZwdRl9q2aX57pq9lDm5iG4e632ld7ZcdAoGAIG6loMn5Qxp6O3DidxuUxkaQXCYM/WHfwp+kP5IRxAtCmb/nldgpebQtngC4vcWXdngRItdh1v9WX6aBWvwLkSdqI2HrL1AGssLvXU50FQGPkQSShXL0cItJg/fDKdP2Aw1+Q8+r2mhfd8TjMAxYgTxuYivck3FPzp2hI99A78I=");

        tokenProvider.init();

        String token = tokenProvider.createToken("5349", "CHLOGIN");

        log.debug(token);

        assertNotNull(token);

    }

}

