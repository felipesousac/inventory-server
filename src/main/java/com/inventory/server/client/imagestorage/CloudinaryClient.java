package com.inventory.server.client.imagestorage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryClient  implements ImageStorageClient {

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    private Cloudinary cloudinary() {
        return new Cloudinary(this.cloudinaryUrl);
    }

    @Override
    public String uploadImage(Long imageId, MultipartFile image) throws IOException {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + image.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(image.getBytes());
            fos.close();

            String PUBLIC_ID = "imageItem:" + imageId;

            Map<?, ?> img = cloudinary().uploader().upload(file, ObjectUtils.asMap("public_id",
                    PUBLIC_ID,
                    "folder",
                    "/itemsImg/"));

            return img.get("url").toString();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}
