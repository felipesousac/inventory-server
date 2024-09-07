package com.inventory.server.infra.exception;

public class CategoryAlreadyCreatedException extends Exception{
    public CategoryAlreadyCreatedException(String message) {
        super(message);
    }
}
