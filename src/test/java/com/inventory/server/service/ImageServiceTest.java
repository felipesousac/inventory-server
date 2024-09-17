package com.inventory.server.service;

import com.inventory.server.infra.exception.FileNotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Test
    void doesNotAllowToUploadFileThatIsNotImage() {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        given(file.getContentType()).willReturn("text");

        // When
        Exception ex = assertThrows(FileNotSupportedException.class, () -> {
            imageService.uploadImage(file);
        });

        // Then
        String expectedMessage = "Invalid file type - " + file.getContentType();
        String actualMessage = ex.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}