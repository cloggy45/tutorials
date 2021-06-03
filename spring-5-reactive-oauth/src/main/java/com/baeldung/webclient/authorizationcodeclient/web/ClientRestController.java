package com.baeldung.webclient.authorizationcodeclient.web;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class ClientRestController {

    private static final String RESOURCE_URI = "http://localhost:2990/jira/rest/oauth2/1.0/client";

    @Autowired
    WebClient webClient;

    @Value("${the.authorization.client-id}")
    private String clientId;

    @Value("${the.authorization.client-secret}")
    private String clientSecret;

    @Value("${the.authorization.token-uri}")
    private String tokenUri;

    @PostMapping("/create-client")
    public String createSpringSecurityClient(@RequestParam("clientId") final String clientId,
                                             @RequestParam("clientSecret") final String clientSecret,
                                             @RequestParam("registrationId") final String registrationId,
                                             @RequestParam("authorizationUri") final String authorizationUri,
                                             @RequestParam("tokenUri") final String tokenUri,
                                             @RequestParam("scope") final String scope) {
        
        System.setProperty("the.authorization.client-id", clientId);

        return "blah";
    }

    @GetMapping("/auth-code")
    Mono<String> useOauthWithAuthCode() {
        Mono<String> retrievedResource = webClient.get()
                .uri(RESOURCE_URI)
                .retrieve()
                .bodyToMono(String.class);
        return retrievedResource.map(string -> "We retrieved the following resource using Oauth: " + string);
    }

    @GetMapping("/auth-code-annotated")
    Mono<String> useOauthWithAuthCodeAndAnnotation(@RegisteredOAuth2AuthorizedClient("bael") OAuth2AuthorizedClient authorizedClient) {
        Mono<String> retrievedResource = webClient.get()
                .uri(RESOURCE_URI)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class);
        return retrievedResource.map(string -> "We retrieved the following resource using Oauth: " + string + ". Principal associated: " +
                                               authorizedClient.getPrincipalName() + ". Token will expire at: " + authorizedClient.getAccessToken()
                                                       .getExpiresAt());
    }

    @GetMapping("/auth-code-explicit-client")
    Mono<String> useOauthWithExpicitClient() {
        Mono<String> retrievedResource = webClient.get()
                .uri(RESOURCE_URI)
                .attributes(clientRegistrationId("bael"))
                .retrieve()
                .bodyToMono(String.class);
        return retrievedResource.map(string -> "We retrieved the following resource using Oauth: " + string);
    }

}
