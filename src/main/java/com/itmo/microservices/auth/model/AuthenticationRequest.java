package com.itmo.microservices.auth.model;

import lombok.Value;

@Value
public class AuthenticationRequest {
    String username;
    String password;
}
