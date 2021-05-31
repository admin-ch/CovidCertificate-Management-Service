package ch.admin.bag.covidcertificate.config;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Base64;

@Configuration
public class RestConfig {

    @Value("${cc-management-service.rest.connectTimeoutSeconds}")
    private int connectTimeout;

    @Value("${cc-management-service.rest.readTimeoutSeconds}")
    private int readTimeout;

    @Value("${app.conn.cc-signing-service.key-store}")
    private String keyStore;

    @Value("${app.conn.cc-signing-service.key-store-password}")
    private String keyStorePassword;

    @Value("${app.conn.cc-signing-service.key-alias}")
    private String keyAlias;

    @Value("${app.conn.cc-signing-service.key-password}")
    private String keyPassword;

    @Value("${app.conn.cc-signing-service.trust-store}")
    private String trustStore;

    @Value("${app.conn.cc-signing-service.trust-store-password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate defaultRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    @Bean
    public RestTemplate signingServiceRestTemplate() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        KeyStore truststore = loadKeyStore(trustStore, trustStorePassword.toCharArray());
        KeyStore store = loadKeyStore(keyStore, keyStorePassword.toCharArray());
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(truststore, null)
                .loadKeyMaterial(store, keyPassword.toCharArray(), (map, socket) -> keyAlias)
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(sslContext)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(client);

        return new RestTemplate(requestFactory);
    }

    private KeyStore loadKeyStore(String base64Keystore,
                                  final char[] storePassword) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStoreInstance = KeyStore.getInstance(KeyStore.getDefaultType());

        try (ByteArrayInputStream inStream = new ByteArrayInputStream(base64Keystore.getBytes())) {
            var decodedKeystoreInputStream = Base64.getMimeDecoder().wrap(inStream);
            keyStoreInstance.load(decodedKeystoreInputStream, storePassword);
        }

        return keyStoreInstance;
    }
}
