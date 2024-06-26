package com.inventory.server.controller;

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
import java.sql.Blob;
import java.sql.SQLException;

@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, UriComponentsBuilder uriBuilder) throws IOException {
        CreateRecordUtil uploadImage = imageService.uploadImage(file, uriBuilder);

        return ResponseEntity.created(uploadImage.getUri()).body(uploadImage.getObject());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) throws FileNotFoundException {
        byte[] imageData = imageService.downloadImage(id);

        return ResponseEntity.ok().contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(imageData);
    }
}
