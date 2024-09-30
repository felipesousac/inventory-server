package com.inventory.server.infra.exception;

public class CategoryAlreadyCreatedException extends RuntimeException {
    public CategoryAlreadyCreatedException(String message) {
        super(message);
    }
}
