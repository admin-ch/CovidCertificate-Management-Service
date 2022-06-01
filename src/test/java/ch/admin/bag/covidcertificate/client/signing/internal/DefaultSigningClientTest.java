package ch.admin.bag.covidcertificate.client.signing.internal;

import ch.admin.bag.covidcertificate.client.signing.SigningInformationDto;
import ch.admin.bag.covidcertificate.client.signing.SigningRequestDto;
import ch.admin.bag.covidcertificate.client.signing.VerifySignatureRequestDto;
import com.flextrade.jfixture.JFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
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
        String url = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "url", url);
        String verifyUrl = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "verifyUrl", verifyUrl);
        String kidUrl = fixture.create(String.class);
        ReflectionTestUtils.setField(signingClient, "kidUrl", kidUrl);
        ResponseEntity byteArrayResponseEntity = mock(ResponseEntity.class);
        lenient().when(byteArrayResponseEntity.getBody()).thenReturn(fixture.create(byte[].class));
        lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(byte[].class))).thenReturn(byteArrayResponseEntity);
        ResponseEntity stringResponseEntity = mock(ResponseEntity.class);
        lenient().when(stringResponseEntity.getBody()).thenReturn(fixture.create(String.class));
        lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class))).thenReturn(stringResponseEntity);
        ResponseEntity booleanResponseEntity = mock(ResponseEntity.class);
        lenient().when(booleanResponseEntity.getBody()).thenReturn(fixture.create(boolean.class));
        lenient().when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(boolean.class))).thenReturn(booleanResponseEntity);
    }

    @Nested
    class CreateSignature{
        @Test
        void makesRequestToCorrectUrl() {
            var url = fixture.create(String.class);
            ReflectionTestUtils.setField(signingClient, "url", url);
            var signingInformation = fixture.create(SigningInformationDto.class);

            signingClient.createSignature(fixture.create(byte[].class), signingInformation);

            verify(restTemplate).exchange(eq(url), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void makesPostRequest() {
            signingClient.createSignature(fixture.create(byte[].class), fixture.create(SigningInformationDto.class));

            verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void makesRequestWithCorrectBody() {
            var body = fixture.create(byte[].class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            var signingRequestDto = new SigningRequestDto(Base64.getEncoder().encodeToString(body),
                                                          signingInformation.getAlias());

            signingClient.createSignature(body, signingInformation);

            verify(restTemplate).exchange(anyString(), any(HttpMethod.class),
                                          argThat(argument -> Objects.equals(argument.getBody(), signingRequestDto)),
                                          any(Class.class));
        }

        @Test
        void returnsResponseBody() {
            ResponseEntity responseEntity = mock(ResponseEntity.class);
            when(responseEntity.getBody()).thenReturn(fixture.create(byte[].class));
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                                       any(Class.class))).thenReturn(responseEntity);

            var actual = signingClient.createSignature(fixture.create(byte[].class),
                                                       fixture.create(SigningInformationDto.class));

            assertEquals(responseEntity.getBody(), actual);
        }

        @Test
        void throwsExceptionIfRequestThrowsException() {
            var exception = fixture.create(RestClientException.class);
            var signingInformation = fixture.create(SigningInformationDto.class);
            var cosePayload = fixture.create(byte[].class);
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

            var actual = assertThrows(RestClientException.class, () -> signingClient.createSignature(cosePayload, signingInformation));

            assertEquals(exception, actual);
        }
    }

    @Nested
    class VerifySignature{
        @Test
        void makesRequestToCorrectUrl() {
            var verifyUrl = fixture.create(String.class);
            ReflectionTestUtils.setField(signingClient, "verifyUrl", verifyUrl);

            signingClient.verifySignature(fixture.create(VerifySignatureRequestDto.class));

            verify(restTemplate).exchange(eq(verifyUrl), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void makesPostRequest() {
            signingClient.verifySignature(fixture.create(VerifySignatureRequestDto.class));

            verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void makesRequestWithCorrectBody() {
            var verifySignatureRequestDto =  fixture.create(VerifySignatureRequestDto.class);

            signingClient.verifySignature(verifySignatureRequestDto);

            verify(restTemplate).exchange(anyString(), any(HttpMethod.class), argThat(argument -> Objects.equals(argument.getBody(), verifySignatureRequestDto)) , any(Class.class));
        }

        @Test
        void returnsResponseBody() {
            ResponseEntity responseEntity = mock(ResponseEntity.class);
            when(responseEntity.getBody()).thenReturn(fixture.create(boolean.class));
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(responseEntity);

            var actual = signingClient.verifySignature(fixture.create(VerifySignatureRequestDto.class));

            assertEquals(responseEntity.getBody(), actual);
        }

        @Test
        void throwsExceptionIfRequestThrowsException() {
            var exception = fixture.create(RestClientException.class);
            var verifySignagureRequestDto = fixture.create(VerifySignatureRequestDto.class);
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

            var actual = assertThrows(RestClientException.class, () -> signingClient.verifySignature(verifySignagureRequestDto));

            assertEquals(exception, actual);
        }
    }

    @Nested
    class GetKeyIdentifier{
        @Test
        void makesRequestToCorrectUrl() {
            var url = UUID.randomUUID().toString();
            var certificateAlias =  fixture.create(String.class);
            var fullUrl = url+"/"+certificateAlias;
            ReflectionTestUtils.setField(signingClient, "kidUrl", url);

            signingClient.getKeyIdentifier(certificateAlias);

            verify(restTemplate).exchange(eq(fullUrl), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void makesGetRequest() {
            signingClient.getKeyIdentifier(fixture.create(String.class));

            verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class));
        }

        @Test
        void returnsResponseBody() {
            ResponseEntity responseEntity = mock(ResponseEntity.class);
            when(responseEntity.getBody()).thenReturn(fixture.create(String.class));
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenReturn(responseEntity);

            var actual = signingClient.getKeyIdentifier(fixture.create(String.class));

            assertEquals(responseEntity.getBody(), actual);
        }

        @Test
        void throwsExceptionIfRequestThrowsException() {
            var exception = fixture.create(RestClientException.class);
            var signingInformation = fixture.create(String.class);
            when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).thenThrow(exception);

            var actual = assertThrows(RestClientException.class, () -> signingClient.getKeyIdentifier(signingInformation));

            assertEquals(exception, actual);
        }
    }
}