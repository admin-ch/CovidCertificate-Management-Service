package ch.admin.bag.covidcertificate.client.valuesets.vaccines;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class VaccineValueSetsClientConfig {
    @Value("${cc-management-service.rest.connectTimeoutSeconds}")
    private int connectTimeout;

    @Bean
    public HttpClient proxyAwareHttpClient() {
        // Config Timeout
        return HttpClient.newBuilder()
                .connectTimeout(Duration.of(connectTimeout, ChronoUnit.SECONDS))
                .proxy(ProxySelector.getDefault())
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }
}
