package com.inventory.server.client.imagestorage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryClient  implements ImageStorageClient {

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    private Cloudinary cloudinary() {
        return new Cloudinary(this.cloudinaryUrl);
    }

    @Override
    public String uploadImage(Long itemId, MultipartFile image) throws IOException {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + image.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(image.getBytes());
            fos.close();

            UUID uuid = UUID.randomUUID();
            String time = LocalDateTime.now().toString();
            String PUBLIC_ID = uuid.toString() + time;

            Map<?, ?> img = cloudinary().uploader().upload(file, ObjectUtils.asMap("public_id",
                    PUBLIC_ID,
                    "folder",
                    "/items_img/"));

            return img.get("url").toString();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary().uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
