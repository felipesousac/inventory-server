package com.inventory.server.service;

import com.inventory.server.infra.exception.FileNotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Test
    void doesNotAllowToUploadFileThatIsNotImage() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("text");

        Exception ex = assertThrows(FileNotSupportedException.class, () -> {
            imageService.uploadImage(file);
        });

        String expectedMessage = "Invalid file type - " + file.getContentType();
        String actualMessage = ex.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }
}