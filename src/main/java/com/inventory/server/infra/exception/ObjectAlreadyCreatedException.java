package com.inventory.server.infra.exception;

public class ObjectAlreadyCreatedException extends RuntimeException {

    public ObjectAlreadyCreatedException(String objName) {
        super("There is an object created with this name: " + objName);
    }
}