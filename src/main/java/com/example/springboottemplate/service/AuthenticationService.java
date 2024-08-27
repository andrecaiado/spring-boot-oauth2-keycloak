package com.example.springboottemplate.service;

import com.example.springboottemplate.config.KeycloakProvider;
import com.example.springboottemplate.dto.LoginRequestDto;
import com.example.springboottemplate.dto.RefreshTokenRequestDto;
import jakarta.ws.rs.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthenticationService {

    private final KeycloakProvider keycloakProvider;

    public AuthenticationService(KeycloakProvider keycloakProvider) {
        this.keycloakProvider = keycloakProvider;
    }

    public ResponseEntity<AccessTokenResponse> login(LoginRequestDto loginRequest) {
        Keycloak keycloak = keycloakProvider.newKeycloakBuilderWithPasswordCredentials(loginRequest.getUsername(), loginRequest.getPassword()).build();

        AccessTokenResponse accessTokenResponse = null;
        try {
            accessTokenResponse = keycloak.tokenManager().getAccessToken();
            return ResponseEntity.status(HttpStatus.OK).body(accessTokenResponse);
        } catch (BadRequestException ex) {
            log.warn("invalid account. User probably hasn't verified email.", ex);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(accessTokenResponse);
        }
    }

    public AccessTokenResponse refreshToken(RefreshTokenRequestDto refreshToken) {
        return keycloakProvider.refreshToken(refreshToken);
    }

}
