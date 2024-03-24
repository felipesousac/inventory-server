package com.inventory.server.infra.exception;

public class ItemAlreadyCreatedException extends Exception {

    public ItemAlreadyCreatedException(String message) {
        super(message);
    }
}
