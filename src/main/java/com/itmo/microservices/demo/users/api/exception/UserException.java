package com.itmo.microservices.demo.users.api.exception;

public abstract class UserException extends Exception {

    public UserException(String message) {
        super(message);
    }
}
