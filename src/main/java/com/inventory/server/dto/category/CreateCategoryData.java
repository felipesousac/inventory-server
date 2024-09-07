package com.inventory.server.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryData(
        @NotBlank
        @Size(max = 30)
        String categoryName,
        @NotBlank
        @Size(max = 50)
        String description
) {
}
