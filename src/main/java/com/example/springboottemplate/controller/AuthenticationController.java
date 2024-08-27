package com.example.springboottemplate.controller;

import com.example.springboottemplate.dto.LoginRequestDto;
import com.example.springboottemplate.dto.RefreshTokenRequestDto;
import com.example.springboottemplate.service.AuthenticationService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody LoginRequestDto loginRequestDto) {
        return authenticationService.login(loginRequestDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        AccessTokenResponse response = authenticationService.refreshToken(refreshTokenRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
