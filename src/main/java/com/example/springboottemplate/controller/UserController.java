package com.example.springboottemplate.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public String hello() {
        return "Hello user!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public String helloAdmin() {
        return "Hello admin!";
    }
}
