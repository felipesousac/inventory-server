package com.inventory.server.client.imagestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageClient {

    String uploadImage(Long imageId,MultipartFile image) throws IOException;
}
