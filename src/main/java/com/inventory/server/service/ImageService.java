package com.inventory.server.service;

import com.inventory.server.domain.ImageRepository;
import com.inventory.server.dto.image.ImageDTOMapper;
import com.inventory.server.dto.image.ImageListData;
import com.inventory.server.infra.exception.FileNotSupportedException;
import com.inventory.server.model.Image;
import com.inventory.server.utils.CreateRecordUtil;
import com.inventory.server.utils.ImageUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageDTOMapper imageDTOMapper;

    public ImageService(ImageRepository imageRepository, ImageDTOMapper imageDTOMapper) {
        this.imageRepository = imageRepository;
        this.imageDTOMapper = imageDTOMapper;
    }

    @Transactional
    public Image uploadImage(MultipartFile imageFile) throws IOException, FileNotSupportedException {
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
}
