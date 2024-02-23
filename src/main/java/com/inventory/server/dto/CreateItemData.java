package com.inventory.server.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateItemData(
                            @NotBlank
                            @Size(max = 30)
                             String itemName,
                            @NotBlank
                            @Size(max = 50)
                             String description,
                             @NotNull
                             Long categoryId,
                             @NotNull
                             @Positive
                             BigDecimal price,
                             @NotNull
                             @Min(value = 0)
                             Integer numberInStock) {
}
