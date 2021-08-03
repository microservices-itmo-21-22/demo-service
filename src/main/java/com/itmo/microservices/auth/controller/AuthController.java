package com.itmo.microservices.auth.controller;

import com.itmo.microservices.auth.model.AuthenticationRequest;
import com.itmo.microservices.auth.model.AuthenticationResult;
import com.itmo.microservices.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public AuthenticationResult authenticate(@RequestBody AuthenticationRequest request) {
        return null;
    }

    @PostMapping("/refresh")
    public AuthenticationResult refresh(Authentication authentication) {
        return null;
    }
}
