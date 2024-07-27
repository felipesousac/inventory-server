package com.inventory.server.utils;

import java.net.URI;

public class CreateRecordUtil {

    private Object object;
    private URI uri;

    public CreateRecordUtil(Object object, java.net.URI uri) {
        this.object = object;
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public Object getObject() {
        return object;
    }
}
