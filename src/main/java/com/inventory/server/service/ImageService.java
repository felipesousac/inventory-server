package com.inventory.server.service;

import com.inventory.server.domain.ImageRepository;
import com.inventory.server.infra.exception.FileNotSupportedException;
import com.inventory.server.model.Image;
import com.inventory.server.utils.ImageUtils;
import io.micrometer.observation.annotation.Observed;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
@Observed(name = "imageService")
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public Image uploadImage(MultipartFile imageFile) throws IOException {
        if (!Objects.requireNonNull(imageFile.getContentType()).contains("image")) {
            throw new FileNotSupportedException("Invalid file type - " + imageFile.getContentType());
        }

        Image image = new Image(imageFile);
        imageRepository.save(image);

        return image;
    }

    public byte[] downloadImage(Long id) throws FileNotFoundException {
        Optional<Image> image = imageRepository.findById(id);

        return image.map(img -> {
            try {
                return ImageUtils.decompressImage(img.getImageData());
            } catch (DataFormatException | IOException ex) {
                throw new ContextedRuntimeException("Error downloading image", ex)
                        .addContextValue("Image ID", id)
                        .addContextValue("Image name", img.getName());
            }
        }).orElseThrow(() -> new FileNotFoundException("File not found"));
    }

    @Transactional
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }
}
