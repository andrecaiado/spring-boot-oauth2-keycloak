package com.example.springboottemplate.config;

import com.example.springboottemplate.dto.RefreshTokenRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public AccessTokenResponse refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {

        RestTemplate restTemplate = new RestTemplate();

        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", refreshTokenRequestDto.getRefreshToken());
        map.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForEntity(url, request, AccessTokenResponse.class).getBody();
    }

    public JsonNode getUserInfo(String bearerToken) {

        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class).getBody();
    }

}
