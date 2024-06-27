package com.inventory.server.controller;

import com.inventory.server.dto.image.ImageDTOMapper;
import com.inventory.server.dto.image.ImageListData;
import com.inventory.server.infra.exception.FileNotSupportedException;
import com.inventory.server.model.Image;
import com.inventory.server.service.ImageService;
import com.inventory.server.utils.CreateRecordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final ImageDTOMapper imageDTOMapper;

    public ImageController(ImageService imageService, ImageDTOMapper imageDTOMapper) {
        this.imageService = imageService;
        this.imageDTOMapper = imageDTOMapper;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, UriComponentsBuilder uriBuilder) throws IOException, FileNotSupportedException {
        Image uploadImage = imageService.uploadImage(file);

        URI uri = uriBuilder.path("/images/{id}").buildAndExpand(uploadImage.getId()).toUri();
        ImageListData imageListData = imageDTOMapper.apply(uploadImage);

        return ResponseEntity.created(uri).body(imageListData);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) throws FileNotFoundException {
        byte[] imageData = imageService.downloadImage(id);

        return ResponseEntity.ok().contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(imageData);
    }
}
