package com.inventory.server.infra.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(Long id) {
        super("Object with id " + id + " not found");
    }
}
