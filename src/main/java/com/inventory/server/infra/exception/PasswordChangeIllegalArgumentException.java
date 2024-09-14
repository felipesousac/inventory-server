package com.inventory.server.infra.exception;

public class PasswordChangeIllegalArgumentException extends RuntimeException {
    public PasswordChangeIllegalArgumentException(String message) {
        super(message);
    }
}
