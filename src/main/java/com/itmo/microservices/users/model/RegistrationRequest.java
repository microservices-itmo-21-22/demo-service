package com.itmo.microservices.users.model;

import lombok.Value;

@Value
public class RegistrationRequest {
    String username;
    String name;
    String surname;
    String email;
    String password;
}
