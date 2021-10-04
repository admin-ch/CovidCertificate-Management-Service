package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.domain.SigningInformation;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSigningClientTest {
    @Spy
    @InjectMocks
    private DefaultSigningClient signingClient;

    @Mock
    private RestTemplate restTemplate;

    private final JFixture fixture = new JFixture();

    @BeforeEach
    public void init(){
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        String url = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "url", url);
        lenient().when(responseEntity.getBody()).thenReturn(fixture.create(byte[].class));
        lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(responseEntity);
    }

    @Test
    void makesRequestToCorrectUrl() {
        var url = UUID.randomUUID().toString();
        var signingInformation =  fixture.create(SigningInformation.class);
        var fullUrl = url+"/"+signingInformation.getAlias();
        ReflectionTestUtils.setField(signingClient, "url", url);

        signingClient.create(fixture.create(byte[].class), signingInformation);

        verify(restTemplate).exchange(eq(fullUrl), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void makesPostRequest() {
        signingClient.create(fixture.create(byte[].class), fixture.create(SigningInformation.class));

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void makesRequestWithCorrectHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_CBOR_VALUE));

        signingClient.create(fixture.create(byte[].class), fixture.create(SigningInformation.class));

        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(argument -> argument.getHeaders().equals(headers)) , any(Class.class));
    }

    @Test
    void makesRequestWithCorrectBody() {
        byte[] body = fixture.create(byte[].class);
        signingClient.create(body, fixture.create(SigningInformation.class));

        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(argument -> Objects.equals(argument.getBody(), body)) , any(Class.class));
    }

    @Test
    void returnsResponseBody() {
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(fixture.create(byte[].class));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(responseEntity);

        var actual = signingClient.create(fixture.create(byte[].class), fixture.create(SigningInformation.class));

        assertEquals(responseEntity.getBody(), actual);
    }

    @Test
    void throwsExceptionIfRequestThrowsException() {
        var exception = fixture.create(RestClientException.class);
        var signingInformation = fixture.create(SigningInformation.class);
        var cosePayload = fixture.create(byte[].class);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

        var actual = assertThrows(RestClientException.class, () -> signingClient.create(cosePayload, signingInformation));

        assertEquals(exception, actual);
    }
}