package com.inventory.server.infra.exception;

public class InvalidJWTAuthenticationException extends Exception {
    public InvalidJWTAuthenticationException(String message) {
        super(message);
    }
}
