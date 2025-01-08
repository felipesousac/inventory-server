package com.inventory.server.infra.exception;

public class UsernameChangeIllegalArgumentException extends RuntimeException {
    public UsernameChangeIllegalArgumentException(String message) {
        super(message);
    }
}
