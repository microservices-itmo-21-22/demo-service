package com.itmo.microservices.auth.model;

import lombok.Value;

@Value
public class AuthenticationResult {
    String accessToken;
    String refreshToken;
}
