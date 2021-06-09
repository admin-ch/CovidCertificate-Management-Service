package ch.admin.bag.covidcertificate.client.printing.config;

import org.springframework.web.reactive.function.client.WebClient;

/**
 * Interface specification for a WebClient builder factory that can create WebClient.Builder instances that build
 * WebClient instances that automatically add an OAuth2 access token as bearer to WebClient exchanges.
 *
 * There are different factory methods for creating WebClients with different possible sources for the access tokens.
 *
 * One possibility is to configure the WebClient as an OAuth2 client for a given client registration and fetch access tokens
 * from an authorization server by means of the OAuth2 client credentials flow. The OAuth2 client configuration must be
 * provided using the standard spring boot security configuration properties (spring.security.oauth2.client.registration.*).
 *
 * Another possibility is to configure the WebClient to carry over the access token from the incoming request it is executed in.
 *
 * A third possibility is to combine the first two possibilities.
 */
public interface JeapOAuth2WebclientBuilderFactory {

    /**
     * Creates a WebClient.Builder instance that is configured to build WebClient instances that augment exchanges
     * with OAuth2 access tokens created from the client configuration identified.
     *
     * @param clientId Identifier to select one of the configured OAuth2 client configurations from the configuration properties.
     * @return A WebClient.Builder instance preconfigured to build WebClient instances that act as an OAuth2 client.
     */
    WebClient.Builder createForClientId(String clientId);

    /**
     * Creates a WebClient.Builder instance that is configured to build WebClient instances that augment exchanges
     * with OAuth2 access tokens. A created WebClient instance carries over the OAuth2 token from the incoming request it is
     * executed in if such a token is present, otherwise the WebClient instance uses an access token created from the
     * client configuration identified.
     *
     * @param clientId Identifier to select one of the configured OAuth2 client configurations from the configuration properties.
     * @return A WebClient.Builder instance preconfigured to build WebClient instances that augment exchanges with OAuth2 access tokens.
     */
    WebClient.Builder createForClientIdPreferringTokenFromIncomingRequest(String clientId);

    /**
     * Creates a WebClient.Builder instance that is configured to build WebClient instances that augment every exchange
     * with the OAuth2 access token carried over from the current incoming request, i.e. a created WebClient instance 'reuses'
     * the OAuth2 access token of the incoming request it is executed in.
     * @return A WebClient.Builder instance preconfigured to build WebClient instances that augment exchanges with OAuth2 access tokens
     *         taken over from the current authentication context.
     */
    WebClient.Builder createForTokenFromIncomingRequest();

}
