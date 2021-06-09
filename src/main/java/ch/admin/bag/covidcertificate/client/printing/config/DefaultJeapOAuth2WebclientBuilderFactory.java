package ch.admin.bag.covidcertificate.client.printing.config;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

public class DefaultJeapOAuth2WebclientBuilderFactory implements JeapOAuth2WebclientBuilderFactory {

    private final WebClient.Builder webClientBuilder;
    private final ExchangeFilterFunction oauth2ClientExchangeFilterFunction;
    private final ExchangeFilterFunction bearerFromAuthenticationExchangeFilterFunction;

    public DefaultJeapOAuth2WebclientBuilderFactory(WebClient.Builder webClientBuilder, ExchangeFilterFunction oauth2ClientExchangeFilterFunction, ExchangeFilterFunction bearerFromAuthenticationExchangeFilterFunction) {
        this.webClientBuilder = webClientBuilder.clone();
        this.oauth2ClientExchangeFilterFunction = oauth2ClientExchangeFilterFunction;
        this.bearerFromAuthenticationExchangeFilterFunction = bearerFromAuthenticationExchangeFilterFunction;
    }

    @Override
    public WebClient.Builder createForClientId(String clientId) {
        assertOAuth2ClientConfigured();
        return webClientBuilder.clone().
                // Make the client id for the OAauth2 exchange filter function known
                filter( (request, next) -> next.exchange(ClientRequest.from(request).attributes(clientRegistrationId(clientId)).build())).
                // Enable OAuth2 bearer token population on exchanges
                filter(oauth2ClientExchangeFilterFunction);
    }

    @Override
    public WebClient.Builder createForClientIdPreferringTokenFromIncomingRequest(String clientId) {
        assertOAuth2ClientConfigured();
        return webClientBuilder.clone().
            // Make the client id for the OAauth2 exchange filter function known
            filter( (request, next) -> next.exchange(ClientRequest.from(request).attributes(clientRegistrationId(clientId)).build())).
            // Use token from current authentication if available
            filter(bearerFromAuthenticationExchangeFilterFunction).
            // Use token from configured OAuth2 client if there was no token available from the current authentication
            filter((request, next) -> {
                if (!request.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return oauth2ClientExchangeFilterFunction.filter(request, next);
                }
                else {
                    return next.exchange(request);
                }
            });
    }

    @Override
    public WebClient.Builder createForTokenFromIncomingRequest() {
        return webClientBuilder.clone().
                filter(bearerFromAuthenticationExchangeFilterFunction);
    }

    private void assertOAuth2ClientConfigured() {
        if (oauth2ClientExchangeFilterFunction == null) {
            throw new UnsupportedOperationException("Application not configured as OAuth2 client.");
        }
    }

}
