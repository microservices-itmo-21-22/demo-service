package com.itmo.microservices.demo.users.api.exception;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
