package com.inventory.server.dto.image;

import com.inventory.server.model.Image;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ImageDTOMapper implements Function<Image, ImageListData> {

    @Override
    public ImageListData apply(Image image) {
        return new ImageListData(
                image.getId(),
                image.getName(),
                image.getType(),
                "File uploaded successfully"
        );
    }
}
