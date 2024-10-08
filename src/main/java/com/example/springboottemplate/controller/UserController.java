package com.example.springboottemplate.controller;

import com.example.springboottemplate.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<JsonNode> getUserInfo(@RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.replace("Bearer ", "");
        return ResponseEntity.ok(userService.getUserInfo(bearerToken));
    }
}
