package com.inventory.server.model;

import com.inventory.server.utils.ImageUtils;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Table(name = "images")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    @Lob
    private byte[] imageData;

    public Image(MultipartFile imageFile) throws IOException {
        this.name = imageFile.getOriginalFilename();
        this.type = imageFile.getContentType();
        this.imageData = ImageUtils.compressImage(imageFile.getBytes());
    }
}
