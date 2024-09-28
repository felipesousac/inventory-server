package com.inventory.server.infra.exception;

public class ItemAlreadyCreatedException extends RuntimeException {

    public ItemAlreadyCreatedException(String message) {
        super(message);
    }
}
