package com.inventory.server.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryData(
        @Size(max = 30, min = 1)
        String categoryName,
        @Size(max = 50)
        String description
) {
}
