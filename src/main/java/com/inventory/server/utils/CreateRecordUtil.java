package com.inventory.server.utils;

import com.inventory.server.model.Item;
import lombok.Getter;

import java.net.URI;

@Getter
public class CreateRecordUtil {

    private Object object;
    private URI uri;

    public CreateRecordUtil(Object object, java.net.URI uri) {
        this.object = object;
        this.uri = uri;
    }
}
