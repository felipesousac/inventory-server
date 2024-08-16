package com.inventory.server.infra.exception;

public class UserAlreadyRegisteredException extends Exception {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }
}
