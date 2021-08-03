package com.itmo.microservices.users.model;

import lombok.Value;

@Value
public class AppUserDto {
    String username;
    String name;
    String surname;
    String email;
}
