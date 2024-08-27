package com.example.springboottemplate.service;

import com.example.springboottemplate.config.KeycloakProvider;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final KeycloakProvider keycloakProvider;

    public UserService(KeycloakProvider keycloakProvider) {
        this.keycloakProvider = keycloakProvider;
    }

    public JsonNode getUserInfo(String bearerToken) {
        return keycloakProvider.getUserInfo(bearerToken);
    }
}
