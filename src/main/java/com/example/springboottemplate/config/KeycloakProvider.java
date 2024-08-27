package com.example.springboottemplate.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KeycloakProvider {

    @Value("${keycloak.url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.clientSecret}")
    private String clientSecret;

//    @Autowired
//    RestTemplate restTemplate;

    private static Keycloak keycloak = null;

    public Keycloak getInstance() {
        if (keycloak == null) {
            return KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.PASSWORD)
                    .build();
        }
        return keycloak;
    }

    public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .username(username)
                .password(password);
    }

//    public JsonNode refreshToken(String refreshToken) {
//        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
//
//        return restTemplate.postForEntity(url, null, JsonNode.class).getBody();
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .field("client_id", clientId)
//                .field("client_secret", clientSecret)
//                .field("refresh_token", refreshToken)
//                .field("grant_type", "refresh_token")
//                .asJson().getBody();
//    }

}
