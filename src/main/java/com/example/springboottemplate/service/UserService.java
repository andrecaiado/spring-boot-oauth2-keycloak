package com.example.springboottemplate.service;

import com.example.springboottemplate.config.KeycloakProvider;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final KeycloakProvider keycloakProvider;

    public UserService(KeycloakProvider keycloakProvider) {
        this.keycloakProvider = keycloakProvider;
    }

    public JsonNode getUserInfo(String bearerToken) {

        String url = keycloakProvider.getServerUrl() + "/realms/" + keycloakProvider.getRealm() + "/protocol/openid-connect/userinfo";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(bearerToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, request, JsonNode.class).getBody();
    }
}
