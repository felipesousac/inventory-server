package com.inventory.server.controller;

import com.inventory.server.dto.image.ImageDTOMapper;
import com.inventory.server.dto.image.ImageListData;
import com.inventory.server.dto.item.ItemListData;
import com.inventory.server.infra.exception.FileNotSupportedException;
import com.inventory.server.model.Image;
import com.inventory.server.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/images")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Images", description = "Endpoints for managing image files")
public class ImageController {

    private final ImageService imageService;
    private final ImageDTOMapper imageDTOMapper;

    public ImageController(ImageService imageService, ImageDTOMapper imageDTOMapper) {
        this.imageService = imageService;
        this.imageDTOMapper = imageDTOMapper;
    }

    @PostMapping
    @Operation(
            summary = "Uploads image",
            description = "Uploads image data to database",
            tags = {"Images"},
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = @Content(schema =
                            @Schema(implementation =
                                    ItemListData.class))
                    ),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content)
            }
    )
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file, UriComponentsBuilder uriBuilder) throws IOException, FileNotSupportedException {
        Image uploadImage = imageService.uploadImage(file);

        URI uri = uriBuilder.path("/images/{id}").buildAndExpand(uploadImage.getId()).toUri();
        ImageListData imageListData = imageDTOMapper.apply(uploadImage);

        return ResponseEntity.created(uri).body(imageListData);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Downloads image",
            description = "Downloads image by id",
            tags = {"Images"},
            responses = {
                    @ApiResponse(
                            description = "Ok",
                            responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.IMAGE_PNG_VALUE
                    )),
                    @ApiResponse(description = "Internal error", responseCode = "500", content = @Content),
                    @ApiResponse(description = "Forbidden", responseCode = "403", content = @Content),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not found", responseCode = "404", content = @Content),
            }
    )
    public ResponseEntity<byte[]> downloadImage(@PathVariable @Parameter(description = "Id of image to " +
            "find") Long id) throws FileNotFoundException {
        byte[] imageData = imageService.downloadImage(id);

        return ResponseEntity.ok().contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE)).body(imageData);
    }
}
