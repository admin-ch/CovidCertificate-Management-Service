package ch.admin.bag.covidcertificate.client.internal;

import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        String url = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "url", url);

        signingClient.create(fixture.create(byte[].class));

        verify(restTemplate).exchange(eq(url), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void makesPostRequest() {
        signingClient.create(fixture.create(byte[].class));

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void makesRequestWithCorrectHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList(MediaType.APPLICATION_CBOR_VALUE));

        signingClient.create(fixture.create(byte[].class));

        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(argument -> argument.getHeaders().equals(headers)) , any(Class.class));
    }

    @Test
    void makesRequestWithCorrectBody() {
        byte[] body = fixture.create(byte[].class);
        signingClient.create(body);

        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(argument -> Objects.equals(argument.getBody(), body)) , any(Class.class));
    }

    @Test
    void returnsResponseBody() {
        ResponseEntity responseEntity = mock(ResponseEntity.class);
        when(responseEntity.getBody()).thenReturn(fixture.create(byte[].class));
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(responseEntity);

        var actual = signingClient.create(fixture.create(byte[].class));

        assertEquals(responseEntity.getBody(), actual);
    }

    @Test
    void throwsExceptionIfRequestThrowsException() {
        var exception = fixture.create(RestClientException.class);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

        var cosePayload = fixture.create(byte[].class);
        var actual = assertThrows(RestClientException.class, () -> signingClient.create(cosePayload));

        assertEquals(exception, actual);
    }
}